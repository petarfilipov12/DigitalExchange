package digital.exchange.egress.hub.cluster.archive.adapter.cluster;

import digital.exchange.egress.hub.cluster.archive.adapter.app.AppLogic;
import digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick.CandlestickUpdaters;
import digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick.MeMessageGateway;
import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.logbuffer.Header;

import org.agrona.DirectBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The clustered service for the auction application.
 */

public class AppClusteredService implements ClusteredService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppClusteredService.class);

    private final CandlestickUpdaters candlestickUpdaters = new CandlestickUpdaters();
    private final MeMessageGateway meMessageGateway = new MeMessageGateway(candlestickUpdaters);

    private final AppLogic appLogic = new AppLogic();
    private final Gateway gateway = new Gateway(appLogic, meMessageGateway);


    @Override
    public void onStart(final Cluster cluster, final Image snapshotImage)
    {
        LOGGER.info("onStart");
    }

    @Override
    public void onSessionOpen(final ClientSession session, final long timestamp)
    {
        LOGGER.info("Client session opened, id: {}", session.id());
    }

    @Override
    public void onSessionClose(final ClientSession session, final long timestamp, final CloseReason closeReason)
    {
        LOGGER.info("Client session closed, id: {}", session.id());
    }

    @Override
    public void onSessionMessage(
        final ClientSession session,
        final long timestamp,
        final DirectBuffer buffer,
        final int offset,
        final int length,
        final Header header)
    {
        LOGGER.info("onSessionMessage");

        gateway.route(buffer, offset, length);
    }

    @Override
    public void onTimerEvent(final long correlationId, final long timestamp)
    {
        LOGGER.info("onTimerEvent");
    }

    @Override
    public void onTakeSnapshot(final ExclusivePublication snapshotPublication)
    {
        LOGGER.info("Take Snapshot");
    }

    @Override
    public void onRoleChange(final Cluster.Role newRole)
    {
        LOGGER.info("Role change: {}", newRole);

        appLogic.onRoleChange(newRole);
    }

    @Override
    public void onTerminate(final Cluster cluster)
    {
        LOGGER.info("Terminating");
        appLogic.onClose();
    }

    @Override
    public int doBackgroundWork(long nowNs) {
        return appLogic.doWork();
    }
}
