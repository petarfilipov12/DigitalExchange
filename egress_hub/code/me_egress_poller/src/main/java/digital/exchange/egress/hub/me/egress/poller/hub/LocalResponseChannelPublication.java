package digital.exchange.egress.hub.me.egress.poller.hub;


import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import io.aeron.Aeron;
import io.aeron.ExclusivePublication;
import org.agrona.DirectBuffer;

public class LocalResponseChannelPublication
{
    private final ExclusivePublication exclusivePublication;

    public LocalResponseChannelPublication()
    {
        final Aeron aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(AppConfig.aeronDirName));

        exclusivePublication = aeron.addExclusivePublication(AppConfig.hubInnerResponseChannel,
                AppConfig.hubInnerResponseChannelStreamId);
    }

    public long offer(final DirectBuffer buffer, final int offset, final int length)
    {
        return exclusivePublication.offer(buffer, offset, length);
    }
}
