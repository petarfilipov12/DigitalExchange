package digital.exchange.egress.hub.me.egress.poller.me.egress.archive;

import io.aeron.ChannelUri;
import io.aeron.ExclusivePublication;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.codecs.SourceLocation;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.samples.archive.RecordingDescriptor;
import io.aeron.samples.archive.RecordingDescriptorCollector;
import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.NoOpLock;
import org.agrona.concurrent.YieldingIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveClientManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveClientManager.class);

    private final String controlResponseChannel;

    private AeronArchive.Context archiveClientContext;
    private AeronArchive archiveClient = null;

    private Subscription subscription = null;

    private ExclusivePublication publication;

    public ArchiveClientManager(final String controlResponseChannel)
    {
        this.controlResponseChannel = controlResponseChannel;
    }

    private void initArchiveClientContext(final String controlChannel, final int controlChannelStreamId)
    {
        archiveClientContext = new AeronArchive.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .controlResponseChannel(controlResponseChannel)
                .controlRequestChannel(controlChannel)
                .controlRequestStreamId(controlChannelStreamId)
                .lock(NoOpLock.INSTANCE);
    }

    private RecordingDescriptor tryGetLastRecording()
    {
        final RecordingDescriptorCollector collector = new RecordingDescriptorCollector(10);
        final int recordCount = archiveClient.listRecordings(0, 10, collector.reset());

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

    private Subscription addSubscription(final String channel, final int streamId)
    {
        IdleStrategy idleStrategy = new YieldingIdleStrategy();
        var aeron = archiveClient.context().aeron();

        Subscription subscription = aeron.addSubscription(channel, streamId);
        while(!subscription.isConnected())
        {
            idleStrategy.idle();
        }

        return subscription;
    }

    private ExclusivePublication addPublication(final String channel, final int streamId)
    {
        IdleStrategy idleStrategy = new YieldingIdleStrategy();
        var aeron = archiveClient.context().aeron();

        ExclusivePublication publication = aeron.addExclusivePublication(channel, streamId);

        while(!publication.isConnected())
        {
            idleStrategy.idle();
        }

        return publication;
    }

    public void startReplay(final String replayChannel, final int replayChannelStreamId, final long position)
    {
        final var recording = tryGetLastRecording();

        if(recording == null)
        {
            //TODO Recording not found
            return;
        }

        final var sessionId = archiveClient.startReplay(
                recording.recordingId(),
                position,
                AeronArchive.NULL_LENGTH,
                replayChannel,
                replayChannelStreamId
        );

        final String channelRead = ChannelUri.addSessionId(replayChannel, (int)sessionId);

        subscription = addSubscription(channelRead, replayChannelStreamId);
    }

    public int poll(final FragmentHandler fragmentHandler)
    {
        if(subscription == null)
        {
            LOGGER.info("subscription == null");
            return -1;
        }
        else if(subscription.isClosed())
        {
            LOGGER.info("subscription is Closed");
            return -1;
        }
        else if(!subscription.isConnected())
        {
            LOGGER.info("subscription is not Connected");
            return -1;
        }

        return subscription.poll(fragmentHandler, 1);
    }

    public void initRecording(final String recordingChannel, final int recordingChannelStreamId)
    {
        final var recording = tryGetLastRecording();

        if(recording == null)
        {
            archiveClient.startRecording(recordingChannel, recordingChannelStreamId, SourceLocation.LOCAL);
        }
        else
        {
            archiveClient.extendRecording(recording.recordingId(), recordingChannel, recordingChannelStreamId,
                    SourceLocation.LOCAL);
        }

        publication = addPublication(recordingChannel, recordingChannelStreamId);
    }

    public long offer(final DirectBuffer buffer, final int offset, final int length)
    {
        if(publication == null)
        {
            return Publication.NOT_CONNECTED;
        }

        return publication.offer(buffer, offset, length);
    }

    public void connect(final String controlChannel, final int controlChannelStreamId)
    {
        if(archiveClient != null)
        {
            archiveClient.close();
            archiveClientContext.close();
        }

        initArchiveClientContext(controlChannel, controlChannelStreamId);

        archiveClient = AeronArchive.connect(archiveClientContext);
    }

    public void disconnect()
    {
        archiveClient.close();
        archiveClientContext.close();
    }
}
