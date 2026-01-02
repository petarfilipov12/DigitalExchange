package digital.exchange.ingress.rest.server.aeron.infra.agents;

import digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientMgr;
import org.agrona.concurrent.Agent;

public class MeKeepAliveAgent implements Agent
{

    @Override
    public int doWork() throws Exception {
        AeronClusterClientMgr.keepAlive();

        return 0;
    }

    @Override
    public String roleName() {
        return "MeClusterClientKeepAliveAgent";
    }
}
