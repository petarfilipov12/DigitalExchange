package digital.exchange.me.infra.cluster;

import io.aeron.archive.Archive;
import io.aeron.archive.ArchiveThreadingMode;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.ConsensusModule;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.MinMulticastFlowControlSupplier;
import io.aeron.driver.ThreadingMode;
import io.aeron.driver.exceptions.ActiveDriverException;
import io.aeron.samples.cluster.ClusterConfig;
import digital.exchange.me.utils.AppConfig;
import digital.exchange.me.utils.Utils;
import org.agrona.CloseHelper;
import org.agrona.concurrent.NoOpLock;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClusterConfigManager implements AutoCloseable
{
    private MediaDriver.Context mediaDriverContext;
    private Archive.Context archiveContext;
    private AeronArchive.Context archiveClientContext;
    private ConsensusModule.Context consensusModuleContext;

    private MediaDriver embeddedMediaDriver;
    private Archive clusterArchive;
    private ConsensusModule clusterConsensusModule;

    private final AppClusteredService appClusteredService;
    private ClusteredServiceContainer.Context clusteredServiceContext;

    private ClusteredServiceContainer clusteredServiceContainer;

    private final boolean useEmbeddedMediaDriver;
    private boolean isMediaDriverRunning = false;

    public ClusterConfigManager(final AppClusteredService appClusteredService, final boolean useEmbeddedMediaDriver)
    {
        this.appClusteredService = appClusteredService;
        this.useEmbeddedMediaDriver = useEmbeddedMediaDriver;

        initContexts();
    }

    private void initContexts()
    {
        final List<String> hostAddresses = List.of(AppConfig.hosts.split(","));

        //await DNS resolution of all the hostnames
        hostAddresses.forEach(Utils::awaitDnsResolution);

        if(useEmbeddedMediaDriver)
        {
            initMediaDriverContext();
        }

        initArchiveContext();
        initConsensusModuleContext();
        initClusteredServiceContext();
    }

    private void initMediaDriverContext()
    {
        mediaDriverContext = new MediaDriver.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .threadingMode(ThreadingMode.SHARED)
                .termBufferSparseFile(true)
                .multicastFlowControlSupplier(new MinMulticastFlowControlSupplier())
                .spiesSimulateConnection(true);
    }

    private void initArchiveContext()
    {
        final AeronArchive.Context replicationArchiveContext = new AeronArchive.Context()
                .controlResponseChannel(AppConfig.aeronWildcardPortChannel)
                .aeronDirectoryName(AppConfig.aeronDirName);

        archiveContext = new Archive.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .archiveDirectoryName(AppConfig.aeronArchiveClusterDirName)
                .controlChannel(AppConfig.aeronArchiveClusterControlChannel)
                .archiveClientContext(replicationArchiveContext)
                .localControlChannel(AppConfig.aeronArchiveClusterLocalControlChannel)
                .localControlStreamId(AppConfig.aeronArchiveClusterLocalControlStreamID)
                .recordingEventsEnabled(false)
                .threadingMode(ArchiveThreadingMode.SHARED)
                .replicationChannel(AppConfig.aeronWildcardPortChannel);
    }

    private void initConsensusModuleContext()
    {
        archiveClientContext = new AeronArchive.Context()
                .lock(NoOpLock.INSTANCE)
                .controlRequestChannel(archiveContext.localControlChannel())
                .controlRequestStreamId(archiveContext.localControlStreamId())
                .controlResponseChannel(archiveContext.localControlChannel())
                .controlResponseStreamId(AppConfig.aeronArchiveClusterLocalResponseStreamID)
                .aeronDirectoryName(AppConfig.aeronDirName);

        final List<String> hostAddresses = List.of(AppConfig.hosts.split(","));
        String clusterMembers = ClusterConfig.clusterMembers(hostAddresses, hostAddresses, AppConfig.portBase);

        consensusModuleContext = new ConsensusModule.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .clusterMemberId(AppConfig.nodeId)
                .clusterMembers(clusterMembers)
                .clusterDirectoryName(AppConfig.aeronClusterDirName)
                .archiveContext(archiveClientContext.clone())
                .ingressChannel(AppConfig.clusterIngressChannel)
                .replicationChannel(AppConfig.aeronWildcardPortChannel)
                .leaderHeartbeatTimeoutNs(TimeUnit.SECONDS.toNanos(3));
    }

    private void initClusteredServiceContext()
    {
        clusteredServiceContext = new ClusteredServiceContainer.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .archiveContext(archiveClientContext.clone())
                .clusterDirectoryName(AppConfig.aeronClusterDirName)
                .clusteredService(appClusteredService);
    }

    public void launchEmbeddedMediaDriver() throws InterruptedException {
        if(!useEmbeddedMediaDriver || isMediaDriverRunning)
        {
            return;
        }

        final long timeoutMillis = 1000;
        final int retries = 30;
        int count;

        for(count = 0; count < retries; count++)
        {
            try {
                embeddedMediaDriver = MediaDriver.launch(mediaDriverContext);
                isMediaDriverRunning = true;

                break;
            } catch (ActiveDriverException e)
            {
                if(!e.getMessage().contains("active driver detected"))
                    throw new ActiveDriverException(e.getMessage());
            }

            Thread.sleep(timeoutMillis);
        }

        if(count >= retries)
        {
            throw new RuntimeException(String.format("Failed to launch Embedded MediaDriver after %d sec",
                    count * timeoutMillis));
        }
    }

    private void launchClusterArchive()
    {
        clusterArchive = Archive.launch(archiveContext);
    }

    private void launchClusterConsensusModule()
    {
        clusterConsensusModule = ConsensusModule.launch(consensusModuleContext);
    }

    private void launchClusteredServiceContainer()
    {
        clusteredServiceContainer = ClusteredServiceContainer.launch(clusteredServiceContext);
    }

    public void launch() throws InterruptedException
    {
        launchEmbeddedMediaDriver();

        launchClusterArchive();
        launchClusterConsensusModule();
        launchClusteredServiceContainer();
    }

    @Override
    public void close() {
        if(useEmbeddedMediaDriver)
        {
            CloseHelper.close(embeddedMediaDriver);
        }

        CloseHelper.closeAll(clusterArchive, clusterConsensusModule, clusteredServiceContainer);
    }
}
