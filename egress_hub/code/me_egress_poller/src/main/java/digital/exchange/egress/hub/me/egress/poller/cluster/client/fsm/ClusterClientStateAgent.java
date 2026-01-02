package digital.exchange.egress.hub.me.egress.poller.cluster.client.fsm;

import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.client.EgressListener;
import io.aeron.exceptions.ConcurrentConcludeException;
import io.aeron.exceptions.RegistrationException;
import io.aeron.exceptions.TimeoutException;
import io.aeron.samples.cluster.ClusterConfig;
import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.NanoClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static digital.exchange.egress.hub.me.egress.poller.cluster.client.fsm.ClusterClientStateAgent.ClusterClientState.*;

public class ClusterClientStateAgent implements Agent
{
    enum ClusterClientState
    {
        CONNECT,
        CONNECTED,

        RECONNECT,

        DISCONNECT,
        DISCONNECTED,
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterClientStateAgent.class);

    private final List<String> clusterHosts;
    private final int clusterPortBase;
    private final String clusterClientEgressChannel;
    private final String clusterClientIngressChannel;

    private final EgressListener egressListener;
    private final ClusterNewLeaderHandler clusterNewLeaderHandler;

    private AeronCluster.Context clusterClientContext;
    private AeronCluster clusterClient;

    private final AtomicReference<ClusterClientState> state = new AtomicReference<>(DISCONNECTED);

    private NanoClock nanoClock;
    private long keepAliveDeadline;
    private final long keepAliveIntervalNs =  TimeUnit.MICROSECONDS.toNanos(500);

    public ClusterClientStateAgent(
            final List<String> clusterHosts,
            final int clusterPortBase,
            final String clusterClientEgressChannel,
            final String clusterClientIngressChannel,
            final EgressListener egressListener,
            final ClusterNewLeaderHandler clusterNewLeaderHandler
    )
    {
        this.clusterHosts = clusterHosts;
        this.clusterPortBase = clusterPortBase;
        this.clusterClientEgressChannel = clusterClientEgressChannel;
        this.clusterClientIngressChannel = clusterClientIngressChannel;

        this.egressListener = egressListener;
        this.clusterNewLeaderHandler = clusterNewLeaderHandler;
    }


    private int stateMachineStep()
    {
        int ret = 0;

        switch (state.get())
        {
            case CONNECT -> ret = connect();
            case CONNECTED -> ret = idleConnected();

            case RECONNECT -> ret = reconnect();

            case DISCONNECT -> ret = disconnect();
            case DISCONNECTED -> ret = idleDisconnected();
        }

        return ret;
    }

    private void initClusterClientContext()
    {
        LOGGER.info("Initializing Aeron Cluster Client Context");

        //clusterHosts.forEach(Utils::awaitDnsResolution);

        final String ingressEndpoints = ClusterConfig.ingressEndpoints(
                clusterHosts,
                clusterPortBase,
                ClusterConfig.CLIENT_FACING_PORT_OFFSET
        );

        clusterClientContext = new AeronCluster.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .egressListener(egressListener)
                .egressChannel(clusterClientEgressChannel)
                .ingressChannel(clusterClientIngressChannel)// ephemeral local port
                .ingressEndpoints(ingressEndpoints);
    }

    private int connect()
    {
        LOGGER.info("Connecting to ME Cluster");

        try {
            initClusterClientContext();

            clusterClient = AeronCluster.connect(clusterClientContext);
        }
        catch (RegistrationException e) {
            LOGGER.info("RegistrationException: {}", e.getMessage());
            return 0;
        }
        catch (ConcurrentConcludeException e) {
            LOGGER.info("ConcurrentConcludeException, restarting aeron client context: {}", e.getMessage());

            state.compareAndSet(CONNECT, RECONNECT);

            return 1;
        }
        catch (TimeoutException e){
            LOGGER.info("TimeoutException, restarting aeron client context: {}", e.getMessage());
            state.compareAndSet(CONNECT, RECONNECT);

            return 1;
        }

        nanoClock = clusterClientContext.aeron().context().nanoClock();

        if(state.compareAndSet(CONNECT, CONNECTED))
        {
            keepAliveDeadline = nanoClock.nanoTime() + keepAliveIntervalNs;

            if(clusterNewLeaderHandler != null)
            {
                clusterNewLeaderHandler.onNewLeader(clusterClient.leaderMemberId());
            }

            LOGGER.info("Connected to Cluster");
        }

        return 1;
    }

    private int reconnect()
    {
        if(clusterClient != null)
        {
            clusterClient.close();
        }
        clusterClientContext.close();

        if(state.compareAndSet(RECONNECT, CONNECT))
        {
            LOGGER.info("Reconnecting");
        }

        return 1;
    }

    private int disconnect()
    {
        LOGGER.info("Disconnecting from Cluster");

        if(clusterClient != null)
        {
            clusterClient.close();
        }
        clusterClientContext.close();

        if(state.compareAndSet(DISCONNECT, DISCONNECTED))
        {
            LOGGER.info("Disconnected");
        }

        return 1;
    }

    private int idleConnected()
    {
        final int ret = clusterClient.pollEgress();

        if( (ret != 0) && clusterClient.isClosed() )
        {
            LOGGER.info("clusterClient is Closed");
            if(state.compareAndSet(CONNECTED, RECONNECT))
            {
                LOGGER.info("setting state to RECONNECT");
            }

            return 1;
        }

        if(nanoClock.nanoTime() - keepAliveDeadline >= 0)
        {
            clusterClient.sendKeepAlive();
            keepAliveDeadline = nanoClock.nanoTime() + keepAliveIntervalNs;
        }

        return ret;
    }

    private int idleDisconnected()
    {
        return 0;
    }

    public boolean connectToCluster()
    {
        return state.compareAndSet(DISCONNECTED, CONNECT);
    }

    public boolean disconnectFromCluster()
    {
        state.set(DISCONNECT);

        return true;
    }

    public long offer(DirectBuffer buffer, final int offset, final int length)
    {
        if(state.get() != CONNECTED)
        {
            return 0;
        }

        return clusterClient.offer(buffer, offset, length);
    }

    @Override
    public void onStart() {
        LOGGER.info("onStart");
    }

    @Override
    public int doWork() {
        return stateMachineStep();
    }

    @Override
    public void onClose() {
        LOGGER.info("onClose");
    }

    @Override
    public String roleName() {
        return "ClusterClientStateAgent";
    }
}
