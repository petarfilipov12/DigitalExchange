package digital.exchange.ingress.rest.server.aeron.infra.egress;

import io.aeron.cluster.client.EgressListener;
import io.aeron.logbuffer.Header;
import digital.exchange.ingress.rest.server.aeron.infra.MsgPrinter;
import org.agrona.DirectBuffer;

public class AeronClientEgressListener implements EgressListener {
    private final EgressMsgHandler egressMsgHandler = new EgressMsgHandler();

    @Override
    public void onMessage(
            long clusterSessionId,
            long timestamp,
            DirectBuffer buffer,
            int offset,
            int length,
            Header header)
    {

        egressMsgHandler.handleMsg(buffer, offset);

        final MsgPrinter msgPrinter = new MsgPrinter();
        System.out.println("EgressListener/onMessage");
        msgPrinter.printMsg(buffer, offset);
    }

    @Override
    public void onNewLeader(long clusterSessionId, long leadershipTermId, int leaderMemberId, String ingressEndpoints) {
        System.out.println("New Leader: " + leaderMemberId);
    }
}
