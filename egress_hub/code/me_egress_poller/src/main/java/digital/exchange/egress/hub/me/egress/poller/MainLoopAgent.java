package digital.exchange.egress.hub.me.egress.poller;

import digital.exchange.egress.hub.me.egress.poller.hub.LocalResponseChannelPublication;
import digital.exchange.egress.hub.me.egress.poller.subscription.fsm.SubscriptionStateAgent;
import digital.exchange.sbe.hub.inner.SignalEnumFromHubNodeDecoder;
import digital.exchange.sbe.hub.inner.MeMessageEncoder;
import digital.exchange.sbe.hub.inner.MessageHeaderEncoder;
import io.aeron.logbuffer.Header;
import digital.exchange.egress.hub.me.egress.poller.hub.SignalDecoder;
import digital.exchange.egress.hub.me.egress.poller.me.egress.archive.MeEgressArchiveClientStateAgent;
import digital.exchange.egress.hub.me.egress.poller.cluster.client.fsm.ClusterClientStateAgent;
import digital.exchange.egress.hub.me.egress.poller.me.egress.MeEgressListener;
import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.concurrent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainLoopAgent implements Agent
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MainLoopAgent.class);
    private final SubscriptionStateAgent hubSubscriptionStateAgent = new SubscriptionStateAgent(
            AppConfig.hubInnerSignalChannel,
            AppConfig.hubInnerSignalChannelStreamId,
            this::hubSubscriptionOnFragment
    );

    private final LocalResponseChannelPublication publication = new LocalResponseChannelPublication();

    private final MeEgressArchiveClientStateAgent meEgressArchiveClientStateAgent =
            new MeEgressArchiveClientStateAgent(this::meEgressArchiveOnFragment);

    private final MeEgressListener meEgressListener = new MeEgressListener(
            meEgressArchiveClientStateAgent::onMeNewLeader);
    private final ClusterClientStateAgent meClusterClientStateAgent = new ClusterClientStateAgent(
            List.of(AppConfig.meHosts.split(",")),
            AppConfig.meClusterPortBase,
            AppConfig.meClusterClientEgressChannel,
            AppConfig.meClusterIngressChannel,
            meEgressListener,
            meEgressArchiveClientStateAgent::onMeNewLeader
    );

    private final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer();
    private long bufferMsgPosition = -1;
    private boolean bufferUpdatedFlag = false;
    private boolean isMsgSent = false;
    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final MeMessageEncoder meMessageEncoder = new MeMessageEncoder();

    private void hubSubscriptionOnFragment(DirectBuffer buffer, int offset, int length, Header header)
    {
        var signal = SignalDecoder.decodeSignal(buffer, offset, length);

        if(signal != null)
        {
            dispatchSignal(signal);
        }
    }

    private void dispatchSignal(final SignalEnumFromHubNodeDecoder signalDecoder)
    {
        final long position = signalDecoder.position();

        switch (signalDecoder.signal())
        {
            case CONNECT -> {
                meClusterClientStateAgent.connectToCluster();
                meEgressArchiveClientStateAgent.connectToMeEgressArchive(position);
            }

            case DISCONNECT -> {
                meClusterClientStateAgent.disconnectFromCluster();
                meEgressArchiveClientStateAgent.disconnectFromMeEgressArchive();
            }

            case DO_WORK -> {
                if(bufferMsgPosition != position)
                {
                    bufferMsgPosition = position;
                    bufferUpdatedFlag = false;
                    isMsgSent = false;
                    meEgressArchiveClientStateAgent.doWork(position);
                }
                else if(!bufferUpdatedFlag)
                {
                    meEgressArchiveClientStateAgent.doWork(position);
                }
                else if(!isMsgSent)
                {
                    sendBuffer();
                }
            }
        }

    }

    private void meEgressArchiveOnFragment(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        LOGGER.info("Offering Msg to Cluster, offset: {}, length: {}, position: {}", offset, length, header.position());

        meMessageEncoder.wrapAndApplyHeader(this.buffer, 0, messageHeaderEncoder)
                .position(header.position())
                .putMessageBytes(buffer, offset, length);

        bufferUpdatedFlag = true;

        sendBuffer();
    }

    private void sendBuffer()
    {
        var res = publication.offer(this.buffer, 0,
                MessageHeaderEncoder.ENCODED_LENGTH + meMessageEncoder.encodedLength());

        if(res > 0)
        {
            isMsgSent = true;
        }
    }

    @Override
    public void onStart() {
        LOGGER.info("onStart");
    }

    @Override
    public int doWork() {
        int ret = hubSubscriptionStateAgent.doWork();
        ret += meClusterClientStateAgent.doWork();

        return ret;
    }

    @Override
    public void onClose() {
        LOGGER.info("onClose");
    }

    @Override
    public String roleName() {
        return "MeEgressArchiveClientStateAgent";
    }

}
