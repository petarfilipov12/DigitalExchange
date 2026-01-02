package digital.exchange.egress.hub.cluster.pusher.subscription.fsm;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import digital.exchange.egress.hub.cluster.pusher.utils.AppConfig;
import org.agrona.concurrent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static digital.exchange.egress.hub.cluster.pusher.subscription.fsm.SubscriptionStateAgent.SubscriptionState.*;

public class SubscriptionStateAgent implements Agent
{
    enum SubscriptionState
    {
        CONNECT,
        CONNECTED,

        RECONNECT,

        DISCONNECT,
        DISCONNECTED,
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionStateAgent.class);

    private final Aeron aeron;

    private final String channel;
    private final int streamId;
    private final FragmentHandler fragmentHandler;

    private final AtomicReference<SubscriptionState> state = new AtomicReference<>(CONNECT);
    private Subscription subscription;

    public SubscriptionStateAgent(final String channel, final int streamId, final FragmentHandler fragmentHandler)
    {
        aeron = Aeron.connect(new Aeron.Context().aeronDirectoryName(AppConfig.aeronDirName));

        this.channel = channel;
        this.streamId = streamId;
        this.fragmentHandler = fragmentHandler;
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

    private int connect()
    {
        LOGGER.info("Connecting Hub Node (Creating subscription)");

        subscription = aeron.addSubscription(channel, streamId);

        state.compareAndSet(CONNECT, CONNECTED);

        return 1;
    }

    private int reconnect()
    {
        if(!subscription.isClosed())
        {
            subscription.close();
        }

        if(state.compareAndSet(RECONNECT, CONNECT))
        {
            LOGGER.info("Reconnecting");
        }

        return 1;
    }

    private int disconnect()
    {
        LOGGER.info("Disconnecting Hub Node");

        if(!subscription.isClosed())
        {
            subscription.close();
        }

        if(state.compareAndSet(DISCONNECT, DISCONNECTED))
        {
            LOGGER.info("Disconnected");
        }

        return 1;
    }

    private int idleConnected()
    {
        final int ret = subscription.poll(fragmentHandler, 1);

        if( (ret != 0) && subscription.isClosed() )
        {
            LOGGER.info("hub subscription is Closed");
            if(state.compareAndSet(CONNECTED, RECONNECT))
            {
                LOGGER.info("setting state to RECONNECT");
            }

            return 1;
        }


        return 0;
    }

    private int idleDisconnected()
    {
        return 0;
    }

    @Override
    public int doWork(){
        return stateMachineStep();
    }

    @Override
    public String roleName() {
        return "HubSubscriptionStateAgent";
    }
}
