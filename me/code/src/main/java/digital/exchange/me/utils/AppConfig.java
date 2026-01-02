package digital.exchange.me.utils;

import io.aeron.samples.cluster.ClusterConfig;

public class AppConfig
{
    /* Aeron Cluster Config */
    public static final int nodeId = Utils.getClusterNode();
    public static final int portBase = Utils.getBasePort();
    public static final String hosts = Utils.getClusterAddresses();
    public static final String hostname = Utils.getHostname();

    public static final String aeronDirName = Utils.getAeronDir();
    public static final String aeronArchiveClusterDirName = Utils.getAeronArchiveClusterDir();
    public static final String aeronClusterDirName = Utils.getAeronClusterDir();

    public static final String clusterIngressChannel = "aeron:udp";

    public static final int aeronArchiveClusterControlChannelPort = portBase + ClusterConfig.ARCHIVE_CONTROL_PORT_OFFSET;
    public static final String aeronArchiveClusterControlChannel =
            "aeron:udp?endpoint=" + hostname + ":" + aeronArchiveClusterControlChannelPort;

    public static final String aeronArchiveClusterLocalControlChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveClusterLocalControlStreamID = 1;

    public static final String aeronArchiveClusterLocalResponseChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveClusterLocalResponseStreamID = 2;

    /* Egress Archive Config */
    public static final String aeronArchiveEgressDirName = Utils.getAeronArchiveEgressDir();
    public static final String recordChannel = "aeron:ipc";
    public static final int recordStreamID = 201;

    public static final int aeronArchiveEgressControlChannelPort = 10001;
    public static final String aeronArchiveEgressControlChannel =
            "aeron:udp?endpoint=" + hostname + ":" + aeronArchiveEgressControlChannelPort;
    public static final int aeronArchiveEgressControlStreamID = 100;

    public static final String aeronArchiveEgressLocalControlChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveEgressLocalControlStreamID = 101;

    public static final String aeronArchiveEgressLocalResponseChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveEgressLocalResponseStreamID = 102;

    public static final String aeronArchiveEgressLocalReplayChannel = "aeron:ipc?term-length=64k";
    public static final int aeronArchiveEgressLocalReplayStreamID = 103;


    /* Common Config */
    public static final String aeronWildcardPortChannel = "aeron:udp?endpoint=" + hostname + ":0";
}
