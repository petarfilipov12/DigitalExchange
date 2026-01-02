package digital.exchange.egress.hub.cluster.archive.adapter.utils;

import io.aeron.samples.cluster.ClusterConfig;

public class AppConfig
{
    /* Common Config */
    public static final String hostname = Utils.getHostname();
    public static final String aeronWildcardPortChannel = "aeron:udp?endpoint=" + hostname + ":0";

    /* Aeron Cluster Config */
    public static final int nodeId = Utils.getClusterNode();
    public static final int portBase = Utils.getBasePort();
    public static final String hosts = Utils.getClusterAddresses();

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

    /* PostgreSql DB Config */
    public static final String dbUrl = "jdbc:postgresql://postgres_node/marketdatadb";
    public static final String dbUser = "admin";
    public static final String dbPassword = "password";

    /* ME Cluster Config */
    public static final String meHosts = Utils.getMeClusterAddresses();
    public static final String[] meNodes = meHosts.split(",");

    public static final int meClusterPortBase = Utils.getMeClusterBasePort();

    public static final String meClusterIngressChannel = "aeron:udp";
    public static final String meClusterClientEgressChannel = aeronWildcardPortChannel;

    public static final int meArchiveControlStreamId = 100;
    public static String getMeNodeArchiveControlChannel(int nodeId)
    {
        return "aeron:udp?endpoint=" + meNodes[nodeId] + ":10001";
    }
    public static final String meNodeArchiveControlResponseChannel = aeronWildcardPortChannel;

    /* Local ME HUB Config */
    public static final String hubInnerSignalChannel = "aeron:ipc";
    public static final int hubInnerSignalChannelStreamId = 1001;

    public static final String hubInnerResponseChannel = "aeron:ipc";
    public static final int hubInnerResponseChannelStreamId = 1002;

    public static final String memMappedMessageFile = Utils.getMemMappedMessageFile();

    public static final int meEgressPollerHubServiceId = 1;
    public static final int hubClusterPusherHubServiceId = 2;
    public static final int cacheOrderBookUpdaterServiceId = 4;
    public static final int cacheAccountUpdaterServiceId = 8;
    public static final int dbTradesUpdaterServiceId = 16;
}
