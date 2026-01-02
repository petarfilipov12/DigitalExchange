package digital.exchange.ingress.rest.server.aeron.infra.agents;

import digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientMgr;
import org.agrona.concurrent.Agent;

public class MeEgressPollAgent implements Agent
{

    @Override
    public int doWork() throws Exception {
        return AeronClusterClientMgr.pollClusterEgress();
    }

    @Override
    public String roleName() {
        return "MeClusterClientPollEgressAgent";
    }
}
