package digital.exchange.ingress.rest.server.aeron.infra;

import digital.exchange.ingress.rest.server.aeron.infra.agents.MeKeepAliveAgent;
import digital.exchange.ingress.rest.server.aeron.infra.agents.AeronClusterClientAgent;
import digital.exchange.ingress.rest.server.aeron.infra.agents.MeEgressPollAgent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;

public class AeronAgentsRunner
{
    private final MeKeepAliveAgent keepAliveAgent = new MeKeepAliveAgent();
    private final IdleStrategy keepAliveIdleStrategy = new SleepingMillisIdleStrategy(300);
    private final AgentRunner keepAliveAgentRunner = new AgentRunner(
            keepAliveIdleStrategy,
            Throwable::printStackTrace,
            null,
            keepAliveAgent
    );

    private final MeEgressPollAgent egressPollAgent = new MeEgressPollAgent();
    private final IdleStrategy egressPollIdleStrategy = new SleepingMillisIdleStrategy();
    private final AgentRunner egressPollAgentRunner = new AgentRunner(
            egressPollIdleStrategy,
            Throwable::printStackTrace,
            null,
            egressPollAgent
    );

    private final AeronClusterClientAgent aeronClusterClientAgent = new AeronClusterClientAgent();
    private final IdleStrategy aeronClusterClientIdleStrategy = new SleepingMillisIdleStrategy();
    private final AgentRunner aeronClusterClientAgentRunner = new AgentRunner(
            aeronClusterClientIdleStrategy,
            Throwable::printStackTrace,
            null,
            aeronClusterClientAgent
    );

    public void start()
    {
        AgentRunner.startOnThread(keepAliveAgentRunner);
        AgentRunner.startOnThread(egressPollAgentRunner);
        AgentRunner.startOnThread(aeronClusterClientAgentRunner);
    }
}
