package digital.exchange.egress.hub.cluster.pusher.utils;

import io.aeron.samples.cluster.ClusterConfig;

public class AppConfig
{
    /* Common Config */
    public static final String hostname = Utils.getHostname();
    public static final String aeronWildcardPortChannel = "aeron:udp?endpoint=" + hostname + ":0";

    public static final String aeronDirName = Utils.getAeronDir();

    /* Me Egress Poller Config */
    public static final int serviceId = 2;

    /* Hub Cluster Config */
    public static final int nodeId = Utils.getClusterNode();
    public static final int portBase = Utils.getBasePort();
    public static final String hosts = Utils.getClusterAddresses();

    public static final String aeronArchiveClusterDirName = Utils.getAeronArchiveClusterDir();
    public static final String aeronClusterDirName = Utils.getAeronClusterDir();

    public static final String clusterIngressChannel = "aeron:udp";
    public static final String clusterClientEgressChannel = aeronWildcardPortChannel;

    public static final int aeronArchiveClusterControlChannelPort = portBase + ClusterConfig.ARCHIVE_CONTROL_PORT_OFFSET;
    public static final String aeronArchiveClusterControlChannel =
            "aeron:udp?endpoint=" + hostname + ":" + aeronArchiveClusterControlChannelPort;

    public static final String aeronArchiveClusterLocalControlChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveClusterLocalControlStreamID = 1;

    public static final String aeronArchiveClusterLocalResponseChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveClusterLocalResponseStreamID = 2;

    /* Cluster Archive Adapter Config */
    public static final String clusterArchiveAdapterMeReplayChannel = "aeron:udp?endpoint=" + hostname + ":11001";
    public static final int clusterArchiveAdapterMeReplayChannelStreamId = 100;

    /* Local ME HUB Config */
    public static final String hubInnerSignalChannel = "aeron:ipc";
    public static final int hubInnerSignalChannelStreamId = 1001;

    public static final String hubInnerResponseChannel = "aeron:ipc";
    public static final int hubInnerResponseChannelStreamId = 1002;
}
