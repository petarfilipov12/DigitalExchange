package digital.exchange.ingress.rest.server.aeron.infra.agents;

import digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientMgr;
import org.agrona.concurrent.Agent;

public class AeronClusterClientAgent implements Agent
{

    @Override
    public int doWork() throws Exception {
        AeronClusterClientMgr.check();

        return 0;
    }

    @Override
    public String roleName() {
        return "AeronClusterClientAgent";
    }
}
