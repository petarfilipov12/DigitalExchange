package digital.exchange.egress.hub.me.egress.poller.me.egress.archive;

import io.aeron.logbuffer.FragmentHandler;
import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static digital.exchange.egress.hub.me.egress.poller.me.egress.archive.MeEgressArchiveClientStateAgent.MeEgressArchiveClientState.*;

public class MeEgressArchiveClientStateAgent
{
    enum MeEgressArchiveClientState
    {
        CONNECT,
        RECONNECT,
        CONNECTED,
        DISCONNECT,
        DISCONNECTED
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MeEgressArchiveClientStateAgent.class);

    private final FragmentHandler fragmentHandler;
    private final ArchiveClientManager archiveClientManager = new ArchiveClientManager(AppConfig.meNodeArchiveControlResponseChannel);

    private final AtomicReference<MeEgressArchiveClientState> state = new AtomicReference<>(DISCONNECTED);
    private int leaderId = -1;
    private long lastPosition = -1;

    private long nextPosition = -1;

    public MeEgressArchiveClientStateAgent(final FragmentHandler fragmentHandler)
    {
        this.fragmentHandler = fragmentHandler;
    }

    private void stateMachineStep()
    {
        switch (state.get())
        {
            case CONNECT -> connect();
            case RECONNECT -> reconnect();
            case DISCONNECT -> disconnect();
        }
    }

    private void connect()
    {
        if(leaderId < 0) return;

        archiveClientManager.connect(AppConfig.getMeNodeArchiveControlChannel(leaderId),
                AppConfig.meArchiveControlStreamId);

        archiveClientManager.startReplay(
                AppConfig.clusterArchiveAdapterMeReplayChannel,
                AppConfig.clusterArchiveAdapterMeReplayChannelStreamId,
                lastPosition
        );

        if(state.compareAndSet(CONNECT, CONNECTED))
        {
            LOGGER.info("Connected to Me Egress Archive, Replay Started");
            nextPosition = -1;
        }
    }

    private void reconnect()
    {
        if(state.compareAndSet(RECONNECT, CONNECT)) LOGGER.info("Reconnecting");
    }

    private void disconnect()
    {
        archiveClientManager.disconnect();

        if(state.compareAndSet(DISCONNECT, DISCONNECTED)) LOGGER.info("Disconnected");
    }

    private int pollEgressArchive()
    {
        if( (nextPosition != -1) && (lastPosition != nextPosition))
        {
            LOGGER.info("lastPosition {} != nextPosition {}. Changing State Machine State to RECONNECT", lastPosition, nextPosition);
            state.compareAndSet(CONNECTED, RECONNECT);

            return 0;
        }

        int ret = archiveClientManager.poll(this::onFragment);

        if(ret < 0)
        {
            LOGGER.info("Poll Error. Changing State Machine State to RECONNECT");
            state.compareAndSet(CONNECTED, RECONNECT);
            return 0;
        }

        return ret;
    }


    public void onMeNewLeader(int leaderId)
    {
        LOGGER.info("New leaderId: {}", leaderId);

        this.leaderId = leaderId;

        boolean stateChanged = false;

        if(state.compareAndSet(CONNECT, RECONNECT))
        {
            stateChanged = true;
        }
        else if(state.compareAndSet(CONNECTED, RECONNECT))
        {
            stateChanged = true;
        }

        if(stateChanged)
        {
            LOGGER.info("Changing State to RECONNECT");
        }
    }

    public void connectToMeEgressArchive(final long position)
    {
        lastPosition = position;
        state.set(CONNECT);
    }

    public void disconnectFromMeEgressArchive()
    {
        state.set(DISCONNECT);
        leaderId = -1;
    }

    public void doWork(final long position)
    {
        lastPosition = position;

        if(state.get() != CONNECTED)
        {
            stateMachineStep();
            return;
        }

        pollEgressArchive();
    }

    private void onFragment(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        nextPosition = header.position();

        fragmentHandler.onFragment(buffer, offset, length, header);
    }
}
