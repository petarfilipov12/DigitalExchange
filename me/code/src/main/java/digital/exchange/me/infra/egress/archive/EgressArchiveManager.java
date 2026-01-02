package digital.exchange.me.infra.egress.archive;

import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import io.aeron.ChannelUri;
import io.aeron.ChannelUriStringBuilder;
import io.aeron.Subscription;
import io.aeron.archive.Archive;
import io.aeron.archive.ArchiveThreadingMode;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.client.ArchiveException;
import io.aeron.archive.codecs.SourceLocation;
import io.aeron.logbuffer.FrameDescriptor;
import io.aeron.logbuffer.Header;
import io.aeron.samples.archive.RecordingDescriptor;
import io.aeron.samples.archive.RecordingDescriptorCollector;
import digital.exchange.me.utils.AppConfig;
import digital.exchange.me.utils.SbeMeOutUtils;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.NoOpLock;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgressArchiveManager implements AutoCloseable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EgressArchiveManager.class);

    private final Archive.Context archiveContext;
    private final AeronArchive.Context aeronArchiveContext;

    private Archive archive;
    private AeronArchive aeronArchiveClient;

    private String publicationChannel = AppConfig.recordChannel;

    public EgressArchiveManager()
    {
        final AeronArchive.Context replicationArchiveContext = new AeronArchive.Context()
                .controlResponseChannel(AppConfig.aeronWildcardPortChannel)
                .aeronDirectoryName(AppConfig.aeronDirName);

        archiveContext = new Archive.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .archiveDirectoryName(AppConfig.aeronArchiveEgressDirName)
                .controlChannel(AppConfig.aeronArchiveEgressControlChannel)
                .controlStreamId(AppConfig.aeronArchiveEgressControlStreamID)
                .replicationChannel(AppConfig.aeronWildcardPortChannel)
                .archiveClientContext(replicationArchiveContext)
                .localControlChannel(AppConfig.aeronArchiveEgressLocalControlChannel)
                .localControlStreamId(AppConfig.aeronArchiveEgressLocalControlStreamID)
                .recordingEventsEnabled(false)
                .threadingMode(ArchiveThreadingMode.SHARED);

        aeronArchiveContext = new AeronArchive.Context()
                .lock(NoOpLock.INSTANCE)
                .aeronDirectoryName(AppConfig.aeronDirName)
                .controlRequestChannel(archiveContext.localControlChannel())
                .controlRequestStreamId(archiveContext.localControlStreamId())
                .controlResponseChannel(archiveContext.localControlChannel())
                .controlResponseStreamId(AppConfig.aeronArchiveEgressLocalResponseStreamID);
    }

    public void launchEgressArchiveManager()
    {
        archive = Archive.launch(archiveContext);
        aeronArchiveClient = AeronArchive.connect(aeronArchiveContext);

        initArchiveRecording();
    }

    public void initArchiveRecording()
    {
        final var recording = tryGetLastRecording();

        if(recording == null)
        {
            LOGGER.info("Recording not found. Starting new recording");
            aeronArchiveClient.startRecording(publicationChannel, AppConfig.recordStreamID, SourceLocation.LOCAL, false);
            return;
        }

        LOGGER.info("Found Record ID: {}", recording.recordingId());

        if(recording.stopPosition() <= 0)
        {
            LOGGER.info("stopPosition {} <= 0. Purging recordId {} and starting new record",
                    recording.stopPosition(), recording.recordingId());

            aeronArchiveClient.purgeRecording(recording.recordingId());
            aeronArchiveClient.startRecording(publicationChannel, AppConfig.recordStreamID, SourceLocation.LOCAL, false);

            LOGGER.info("Record count: {}", getRecordCount());

            return;
        }

        LOGGER.info("Extending existing recording, recording ID: {}", recording.recordingId());

        publicationChannel = new ChannelUriStringBuilder(AppConfig.recordChannel)
                .initialPosition(recording.startPosition(), recording.initialTermId(), recording.termBufferLength())
                .mtu(recording.mtuLength())
                .build();

        aeronArchiveClient.extendRecording(recording.recordingId(), publicationChannel, AppConfig.recordStreamID,
                SourceLocation.LOCAL, false);

        tryGetLastMsgSeqNum(recording.recordingId(), recording.startPosition(), recording.stopPosition());
    }

    private RecordingDescriptor tryGetLastRecording()
    {
        final RecordingDescriptorCollector collector = new RecordingDescriptorCollector(10);
        final int recordCount = aeronArchiveClient.listRecordings(0, 10, collector.reset());

        if(recordCount <= 0)
        {
            return null;
        }
        if(recordCount > 1)
        {
            //TODO error
            LOGGER.error("Existing record count > 1: {}", recordCount);
        }

        return collector.descriptors().getFirst();
    }

    private int getRecordCount()
    {
        final RecordingDescriptorCollector collector = new RecordingDescriptorCollector(10);

        return aeronArchiveClient.listRecordings(0, 10, collector.reset());
    }

    private void tryGetLastMsgSeqNum(final long recordingId, final long startPosition, final long stopPosition)
    {
        //Ugly Hack to find last msg in record

        long position = stopPosition - FrameDescriptor.FRAME_ALIGNMENT;

        if(position < startPosition)
        {
            position = startPosition;
        }

        long sessionId = -1;

        while(true)
        {
            try {
                sessionId = aeronArchiveClient.startReplay(recordingId, position, (stopPosition - position),
                        AppConfig.aeronArchiveEgressLocalReplayChannel, AppConfig.aeronArchiveEgressLocalReplayStreamID);

                break;
            } catch (ArchiveException e) {
                if(!e.getMessage().contains("position not aligned to a data header"))
                {
                    throw new ArchiveException(e.getMessage());
                }
            }

            position -= FrameDescriptor.FRAME_ALIGNMENT;

            if(position < startPosition)
            {
                //TODO error startPosition skipped
                LOGGER.error("position {} < startPosition {}", position, startPosition);
                return;
            }
        }

        final String channelRead = ChannelUri.addSessionId(AppConfig.aeronArchiveEgressLocalReplayChannel, (int)sessionId);

        final Subscription subscription = aeronArchiveClient.context().aeron().addSubscription(channelRead,
                AppConfig.aeronArchiveEgressLocalReplayStreamID);

        final IdleStrategy idleStrategy = new SleepingIdleStrategy();

        while (!subscription.isConnected())
        {
            idleStrategy.idle();
        }

        subscription.poll(this::archiveSeqNumReader, 1);

        subscription.close();

        aeronArchiveClient.stopReplay(sessionId);

        LOGGER.info("tryGetLastMsgSeqNum Finished.");
    }

    private void archiveSeqNumReader(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        final long seqNum = SbeMeOutUtils.getEventSeqNum(buffer, offset, length);
        RuntimeContext.setSeqNum(seqNum);
    }

    public String getPublicationChannel()
    {
        return publicationChannel;
    }

    @Override
    public void close()
    {
        CloseHelper.closeAll(aeronArchiveClient, archive);
    }
}
