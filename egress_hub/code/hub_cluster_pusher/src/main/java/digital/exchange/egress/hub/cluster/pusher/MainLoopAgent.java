package digital.exchange.egress.hub.cluster.pusher;

import digital.exchange.sbe.hub.inner.SignalEnumFromHubNodeDecoder;
import io.aeron.logbuffer.Header;
import digital.exchange.egress.hub.cluster.pusher.hub.HubEgressListener;
import digital.exchange.egress.hub.cluster.pusher.hub.SignalDecoder;
import digital.exchange.egress.hub.cluster.pusher.subscription.fsm.SubscriptionStateAgent;
import digital.exchange.egress.hub.cluster.pusher.cluster.client.fsm.ClusterClientStateAgent;
import digital.exchange.egress.hub.cluster.pusher.utils.AppConfig;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainLoopAgent implements Agent
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MainLoopAgent.class);
    private final SubscriptionStateAgent hubSignalSubscriptionStateAgent = new SubscriptionStateAgent(
            AppConfig.hubInnerSignalChannel,
            AppConfig.hubInnerSignalChannelStreamId,
            this::hubSignalSubscriptionOnFragment
    );

    private final SubscriptionStateAgent hubResponseSubscriptionStateAgent = new SubscriptionStateAgent(
            AppConfig.hubInnerResponseChannel,
            AppConfig.hubInnerResponseChannelStreamId,
            this::hubResponseSubscriptionOnFragment
    );

    private final ClusterClientStateAgent hubClusterClientStateAgent = new ClusterClientStateAgent(
            List.of(AppConfig.hosts.split(",")),
            AppConfig.portBase,
            AppConfig.clusterClientEgressChannel,
            AppConfig.clusterIngressChannel,
            new HubEgressListener(),
            null
    );

    private int workDone = 0;

    private void hubSignalSubscriptionOnFragment(DirectBuffer buffer, int offset, int length, Header header)
    {
        var signal = SignalDecoder.decodeSignal(buffer, offset, length);

        if(signal != null)
        {
            dispatchSignal(signal);
        }
    }

    private void dispatchSignal(final SignalEnumFromHubNodeDecoder signalDecoder)
    {
        switch (signalDecoder.signal())
        {
            case CONNECT -> {
                hubClusterClientStateAgent.connectToCluster();
            }

            case DISCONNECT -> {
                hubClusterClientStateAgent.disconnectFromCluster();
            }

            case DO_WORK -> {
                workDone += hubResponseSubscriptionStateAgent.doWork();
            }
        }

    }

    private void hubResponseSubscriptionOnFragment(DirectBuffer buffer, int offset, int length, Header header)
    {
        LOGGER.info("Offering Msg to Cluster");

        hubClusterClientStateAgent.offer(buffer, offset, length);
    }

    @Override
    public void onStart() {
        LOGGER.info("onStart");
    }

    @Override
    public int doWork(){
        int ret = 0;

        ret += hubSignalSubscriptionStateAgent.doWork();
        ret += hubClusterClientStateAgent.doWork();

        ret += workDone;

        workDone = 0;

        return ret;
    }

    @Override
    public void onClose() {
        LOGGER.info("onClose");
    }

    @Override
    public String roleName() {
        return "MainLoopAgent";
    }
}
