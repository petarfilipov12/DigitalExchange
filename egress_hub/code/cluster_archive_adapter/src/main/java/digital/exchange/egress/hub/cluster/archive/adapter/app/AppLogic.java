package digital.exchange.egress.hub.cluster.archive.adapter.app;

import digital.exchange.egress.hub.cluster.archive.adapter.app.hubServices.HubServices;
import digital.exchange.egress.hub.cluster.archive.adapter.utils.AppConfig;
import digital.exchange.sbe.hub.inner.SignalEnum;
import io.aeron.cluster.service.Cluster;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static digital.exchange.egress.hub.cluster.archive.adapter.app.AppLogic.StateMachineStateEnum.*;

public class AppLogic implements Agent
{
    enum StateMachineStateEnum
    {
        UPDATE_DATABASES,
        POLLING_EGRESS,
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogic.class);

    private final MessageMappedBuffer messageMappedBuffer = new MessageMappedBuffer(AppConfig.memMappedMessageFile, 256);
    private final LocalSignalChannelPublication localSignalChannelPublication = new LocalSignalChannelPublication();
    private final HubServices hubServices = new HubServices();

    private StateMachineStateEnum state = POLLING_EGRESS;

    private long currentPosition = 0;

    private boolean isLeader = false;

    public void onMeMessage(DirectBuffer buffer, final int offset, final int length, final long position)
    {
        if(position <= this.currentPosition)
        {
            return;
        }

        this.currentPosition = position;
        messageMappedBuffer.writeMsgToMappedBuffer(buffer, offset, length);

        state = UPDATE_DATABASES;
    }

    public void onPositionUpdate(final int hubServiceId, final long position)
    {
        if(position >= currentPosition)
        {
            hubServices.updatePosition(hubServiceId, position);
        }
    }

    public void onRoleChange(final Cluster.Role newRole)
    {
        if(newRole == Cluster.Role.LEADER)
        {
            localSignalChannelPublication.sendCommand(Integer.MAX_VALUE, currentPosition, SignalEnum.CONNECT);
            isLeader = true;
        }
        else
        {
            localSignalChannelPublication.sendCommand(Integer.MAX_VALUE, currentPosition, SignalEnum.DISCONNECT);
            isLeader = false;
        }
    }

    private int stateMachineStep()
    {
        int ret = 0;

        switch (state)
        {
            case POLLING_EGRESS -> ret += pollMeEgress();
            case UPDATE_DATABASES -> ret += updateDatabases();
        }

        return ret;
    }

    private int pollMeEgress()
    {
        return sendDoWorkCommand(AppConfig.meEgressPollerHubServiceId, currentPosition);
    }

    private int updateDatabases()
    {
        final int serviceIdMask = hubServices.getNotUpdatedServiceIdMask(currentPosition);

        if(serviceIdMask <= 0)
        {
            state = POLLING_EGRESS;
            return 1;
        }

        return sendDoWorkCommand(serviceIdMask, currentPosition);
    }

    private int sendDoWorkCommand(int serviceIdMask, final long position)
    {
        serviceIdMask = serviceIdMask | AppConfig.hubClusterPusherHubServiceId;

        if(isLeader) localSignalChannelPublication.sendCommand(serviceIdMask, position, SignalEnum.DO_WORK);

        return 0;
    }

    @Override
    public int doWork(){
        return stateMachineStep();
    }

    @Override
    public String roleName() {
        return "AppLogic";
    }

    @Override
    public void onClose() {
        localSignalChannelPublication.sendCommand(Integer.MAX_VALUE, currentPosition, SignalEnum.DISCONNECT);
    }
}
