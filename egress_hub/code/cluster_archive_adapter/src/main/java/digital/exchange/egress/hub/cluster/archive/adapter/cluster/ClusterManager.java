package digital.exchange.egress.hub.cluster.archive.adapter.cluster;

import org.agrona.concurrent.ShutdownSignalBarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterManager.class);

    private final AppClusteredService appClusteredService = new AppClusteredService();
    private final ClusterConfigManager clusterConfigManager = new ClusterConfigManager(appClusteredService, false);

    public void start() throws Exception {
        try (ShutdownSignalBarrier barrier = new ShutdownSignalBarrier()) {
            clusterConfigManager.launch();
            barrier.await();
        }
        finally
        {
            LOGGER.info("Exiting");
            clusterConfigManager.close();
        }
    }
}
