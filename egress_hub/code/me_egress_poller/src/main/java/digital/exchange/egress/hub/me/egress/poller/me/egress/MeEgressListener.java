package digital.exchange.egress.hub.me.egress.poller.me.egress;

import digital.exchange.egress.hub.me.egress.poller.cluster.client.fsm.ClusterNewLeaderHandler;
import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.codecs.EventCode;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeEgressListener implements EgressListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MeEgressListener.class);
    private final ClusterNewLeaderHandler newLeaderHandler;

    public MeEgressListener(final ClusterNewLeaderHandler newLeaderHandler)
    {
        this.newLeaderHandler = newLeaderHandler;
    }

    @Override
    public void onMessage(long clusterSessionId, long timestamp, DirectBuffer buffer, int offset, int length, Header header) {
        LOGGER.warn("Receive Msg, Should not get here");
    }

    @Override
    public void onSessionEvent(long correlationId, long clusterSessionId, long leadershipTermId, int leaderMemberId, EventCode code, String detail) {
        LOGGER.info("Session Event: {}, msg: {}", code, detail);
    }

    @Override
    public void onNewLeader(long clusterSessionId, long leadershipTermId, int leaderMemberId, String ingressEndpoints) {
        LOGGER.info("New Leader: {}", leaderMemberId);

        newLeaderHandler.onNewLeader(leaderMemberId);
    }
}
