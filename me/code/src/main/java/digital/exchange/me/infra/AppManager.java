package digital.exchange.me.infra;

import digital.exchange.me.app.AppLogic;
import digital.exchange.me.app.ClockUpdateEventEmitter;
import digital.exchange.me.app.event.emitter.EventEmitter;
import digital.exchange.me.infra.cluster.AppClusteredService;
import digital.exchange.me.infra.cluster.ClockUpdater;
import digital.exchange.me.infra.cluster.ClusterConfigManager;
import digital.exchange.me.infra.cluster.Gateway;
import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import digital.exchange.me.infra.egress.EgressEventHandler;
import digital.exchange.me.infra.egress.EgressPublisher;
import digital.exchange.me.infra.egress.archive.EgressArchiveManager;
import digital.exchange.me.infra.egress.archive.EgressArchivePublisher;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

    private final EventEmitter eventEmitter = new EventEmitter();
    private final EgressPublisher egressPublisher = new EgressPublisher();
    private final EgressArchivePublisher egressArchivePublisher = new EgressArchivePublisher();
    private final EgressEventHandler egressEventHandler = new EgressEventHandler(egressPublisher, egressArchivePublisher);

    private final AppLogic appLogic = new AppLogic(eventEmitter);

    private final ClockUpdateEventEmitter clockUpdateEventEmitter = new ClockUpdateEventEmitter(eventEmitter);
    private final ClockUpdater clockUpdater = new ClockUpdater(clockUpdateEventEmitter);

    private final Gateway gateway = new Gateway(appLogic);

    private final AppClusteredService appClusteredService = new AppClusteredService(gateway, clockUpdater);

    private final ClusterConfigManager clusterConfigManager = new ClusterConfigManager(appClusteredService, true);
    private final EgressArchiveManager egressArchiveManager = new EgressArchiveManager();

    public AppManager()
    {
        if(!eventEmitter.addEventHandler(appLogic::eventHandler))
        {
            LOGGER.error("Failed to add eventHandler: appLogic::eventHandler");
        }

        if(!eventEmitter.addEventHandler(egressEventHandler::eventHandler))
        {
            LOGGER.error("Failed to add eventHandler: egressArchiveEventHandler::eventHandler");
        }
    }

    public void start() throws Exception {
        try
        {
            final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

            clusterConfigManager.launchEmbeddedMediaDriver();

            egressArchiveManager.launchEgressArchiveManager();

            egressArchivePublisher.launch(egressArchiveManager.getPublicationChannel());

            clusterConfigManager.launch();

            LOGGER.info("Started Cluster Node...");
            LOGGER.info("SeqNum: {}", RuntimeContext.getSeqNum());
            barrier.await();
            LOGGER.info("Exiting");

        }
        finally
        {
            clusterConfigManager.close();
            egressArchiveManager.close();
            egressArchivePublisher.close();
        }
    }
}
