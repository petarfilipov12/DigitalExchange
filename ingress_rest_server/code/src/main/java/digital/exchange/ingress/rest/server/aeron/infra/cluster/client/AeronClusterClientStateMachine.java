package digital.exchange.ingress.rest.server.aeron.infra.cluster.client;

import io.aeron.cluster.client.AeronCluster;
import io.aeron.driver.MediaDriver;
import io.aeron.exceptions.ConcurrentConcludeException;
import io.aeron.exceptions.RegistrationException;
import io.aeron.exceptions.TimeoutException;
import io.aeron.samples.cluster.ClusterConfig;
import digital.exchange.ingress.rest.server.utils.AppConfig;
import digital.exchange.ingress.rest.server.aeron.infra.egress.AeronClientEgressListener;
import digital.exchange.ingress.rest.server.utils.Utils;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientStateMachine.AeronClusterClientState.*;

class AeronClusterClientStateMachine
{
    enum AeronClusterClientState
    {
        INIT_MD_CONTEXT,
        LAUNCH_MD,
        INIT_CLIENT_CONTEXT,
        DISCONNECT,
        CONNECT,
        CONNECTED
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(AeronClusterClientStateMachine.class);

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final AtomicReference<AeronClusterClientState> state = new AtomicReference<>(INIT_MD_CONTEXT);

    private MediaDriver.Context mediaDriverContext;
    private MediaDriver mediaDriver;

    private final AeronClientEgressListener egressListener = new AeronClientEgressListener();
    private AeronCluster.Context clientContext;
    private AeronCluster client;

    private final Runnable stateMachineRunnable;
    private final IdleStrategy stateMachineIdleStrategy = new SleepingMillisIdleStrategy();

    public AeronClusterClientStateMachine()
    {
        stateMachineRunnable = () -> {
            while(state.get() != CONNECTED)
            {
                stateMachineIdleStrategy.idle(stateMachineStep());
            }

            LOGGER.info("State Machine finished. Client SHOULD be connected");
            isRunning.set(false);
        };

        startStateMachine(INIT_MD_CONTEXT);
    }

    public void startStateMachine(AeronClusterClientState state)
    {
        if(isRunning.compareAndSet(false, true))
        {
            LOGGER.info("Starting State Machine, state: {}", state);

            this.state.set(state);

            Thread t = new Thread(stateMachineRunnable);
            t.start();
        }
    }

    public AeronClusterClientState getState()
    {
        return state.get();
    }

    public AeronCluster getClient()
    {
        return client;
    }

    private int stateMachineStep()
    {
        int ret = 0;

        switch (state.get())
        {
            case INIT_MD_CONTEXT -> {
                if(initMediaDriverContext())
                {
                    state.set(LAUNCH_MD);
                    ret++;
                }
            }

            case LAUNCH_MD -> {
                if(launchMediaDriver())
                {
                    state.set(INIT_CLIENT_CONTEXT);
                    ret++;
                }
            }

            case INIT_CLIENT_CONTEXT -> {
                if(initClusterClientContext())
                {
                    state.set(CONNECT);
                    ret++;
                }
            }

            case CONNECT -> {
                if(connectToME())
                {
                    state.set(CONNECTED);
                    ret++;
                }
            }

            case DISCONNECT -> {
                if(disconnectFromME())
                {
                    state.set(INIT_CLIENT_CONTEXT);
                    ret++;
                }
            }
        }

        return ret;
    }

    private boolean initMediaDriverContext()
    {
        LOGGER.info("Initializing Aeron MD Context");

        mediaDriverContext = new MediaDriver.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .dirDeleteOnStart(true);

        return true;
    }

    private boolean launchMediaDriver()
    {
        LOGGER.info("Launching Aeron MD");

        mediaDriver = MediaDriver.launchEmbedded(mediaDriverContext);

        return true;
    }

    private boolean initClusterClientContext()
    {
        LOGGER.info("Initializing Aeron Cluster Client Context");

        final List<String> hostAddresses = List.of(AppConfig.meHosts.split(","));
        hostAddresses.forEach(Utils::awaitDnsResolution);

        final String ingressEndpoints = ClusterConfig.ingressEndpoints(
                hostAddresses,
                AppConfig.portBase,
                ClusterConfig.CLIENT_FACING_PORT_OFFSET
        );

        clientContext = new AeronCluster.Context()
                .aeronDirectoryName(mediaDriverContext.aeronDirectoryName())
                .egressListener(egressListener) // ingress endpoint
                .egressChannel(AppConfig.meClusterClientEgressChannel)
                .ingressChannel(AppConfig.meClusterIngressChannel)// ephemeral local port
                .ingressEndpoints(ingressEndpoints);

        return true;
    }

    private boolean connectToME()
    {
        LOGGER.info("Connecting to ME Cluster");

        try {
            client = AeronCluster.connect(clientContext);
            return true;
        }
        catch (RegistrationException e) {
            LOGGER.info("RegistrationException: {}", e.getMessage());
            return false;
        }
        catch (ConcurrentConcludeException e) {
            LOGGER.info("ConcurrentConcludeException, restarting aeron client context: {}", e.getMessage());
            clientContext.close();
            state.set(INIT_CLIENT_CONTEXT);

            return false;
        }
        catch (TimeoutException e){
            LOGGER.info("TimeoutException, restarting aeron client context: {}", e.getMessage());
            clientContext.close();
            state.set(INIT_CLIENT_CONTEXT);

            return false;
        }
    }

    private boolean disconnectFromME()
    {
        LOGGER.info("Disconnecting from ME Cluster");

        client.close();
        clientContext.close();

        return true;
    }
}
