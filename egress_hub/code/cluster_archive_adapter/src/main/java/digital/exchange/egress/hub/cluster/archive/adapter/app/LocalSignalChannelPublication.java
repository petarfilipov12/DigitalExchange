package digital.exchange.egress.hub.cluster.archive.adapter.app;


import digital.exchange.sbe.hub.inner.SignalEnum;
import digital.exchange.sbe.hub.inner.SignalEnumFromHubNodeEncoder;
import digital.exchange.sbe.hub.inner.MessageHeaderEncoder;
import io.aeron.Aeron;
import io.aeron.ExclusivePublication;
import digital.exchange.egress.hub.cluster.archive.adapter.utils.AppConfig;
import org.agrona.ExpandableDirectByteBuffer;

public class LocalSignalChannelPublication
{
    private final ExclusivePublication exclusivePublication;

    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final SignalEnumFromHubNodeEncoder signalEncoder = new SignalEnumFromHubNodeEncoder();
    private final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();

    public LocalSignalChannelPublication()
    {
        final Aeron aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(AppConfig.aeronDirName));

        exclusivePublication = aeron.addExclusivePublication(AppConfig.hubInnerSignalChannel,
                AppConfig.hubInnerSignalChannelStreamId);

        signalEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder);
    }

    public long sendCommand(final int hubServiceIdMask, final long position, final SignalEnum signal)
    {
        signalEncoder.hubServiceIdMask(hubServiceIdMask)
                .position(position)
                .signal(signal);

        return exclusivePublication.offer(buffer, 0,
                MessageHeaderEncoder.ENCODED_LENGTH + signalEncoder.encodedLength());
    }
}
