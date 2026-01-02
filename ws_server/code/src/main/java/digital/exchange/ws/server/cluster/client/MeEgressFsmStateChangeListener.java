package digital.exchange.ws.server.cluster.client;

import digital.exchange.sbe.me.input.EgressSubscribeEncoder;
import digital.exchange.sbe.me.input.MessageHeaderEncoder;
import digital.exchange.ws.server.cluster.client.fsm.ClusterClientFsmAgent;
import digital.exchange.ws.server.cluster.client.fsm.ClusterClientFsmStateChangeListener;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;

public class MeEgressFsmStateChangeListener implements ClusterClientFsmStateChangeListener {
    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final EgressSubscribeEncoder egressSubscribeEncoder = new EgressSubscribeEncoder();

    private final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();
    private final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);

    @Override
    public void onConnected(ClusterClientFsmAgent clusterClientFsmAgent) {
        egressSubscribeEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder);

        final int length = MessageHeaderEncoder.ENCODED_LENGTH + egressSubscribeEncoder.encodedLength();

        while(clusterClientFsmAgent.offer(buffer, 0, length) <= 0)
        {
            idleStrategy.idle();
        }
    }
}
