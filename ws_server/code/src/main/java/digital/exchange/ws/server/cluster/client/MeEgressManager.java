package digital.exchange.ws.server.cluster.client;

import digital.exchange.ws.server.cluster.client.aeron.AeronMdEmbeddedLauncher;
import digital.exchange.ws.server.cluster.client.fsm.ClusterClientFsmAgent;
import digital.exchange.ws.server.message.pipe.MessagePipe;
import digital.exchange.ws.server.utils.AppConfig;
import org.agrona.CloseHelper;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;

public class MeEgressManager implements Closeable
{
    private final AeronMdEmbeddedLauncher aeronMdEmbeddedLauncher = new AeronMdEmbeddedLauncher();

    private final MeEgressListener meEgressListener;
    private final MeEgressFsmStateChangeListener fsmStateChangeListener = new MeEgressFsmStateChangeListener();
    private final ClusterClientFsmAgent meClusterClientFsmAgent;
    private final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
    private final AgentRunner agentRunner;

    public MeEgressManager(final MessagePipe messagePipe)
    {
        meEgressListener = new MeEgressListener(messagePipe);

        meClusterClientFsmAgent = new ClusterClientFsmAgent(
                List.of(AppConfig.meHosts.split(",")),
                AppConfig.meClusterPortBase,
                AppConfig.meClusterClientEgressChannel,
                AppConfig.meClusterIngressChannel,
                meEgressListener,
                fsmStateChangeListener
        );

        agentRunner = new AgentRunner(
                idleStrategy,
                Throwable::printStackTrace,
                null,
                meClusterClientFsmAgent
        );
    }

    public void startOnThread()
    {
        aeronMdEmbeddedLauncher.startAeronMd();
        idleStrategy.idle();

        meClusterClientFsmAgent.connectToCluster();
        AgentRunner.startOnThread(agentRunner);
    }

    public Agent getAgent() {
        return meClusterClientFsmAgent;
    }

    @Override
    public void close()
    {
        CloseHelper.closeAll(agentRunner, aeronMdEmbeddedLauncher);
    }
}
