package digital.exchange.egress.hub.me.egress.poller.hub;

import io.aeron.cluster.client.EgressListener;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HubEgressListener implements EgressListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HubEgressListener.class);

    @Override
    public void onMessage(long clusterSessionId, long timestamp, DirectBuffer buffer, int offset, int length, Header header) {
        LOGGER.warn("Receive Msg, Should not get here");
    }
}
