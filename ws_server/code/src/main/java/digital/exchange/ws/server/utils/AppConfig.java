package digital.exchange.ws.server.utils;

import io.aeron.samples.cluster.ClusterConfig;

public class AppConfig
{
    /* Common Config */
    public static final String hostname = Utils.getHostname();
    public static final String aeronWildcardPortChannel = "aeron:udp?endpoint=" + hostname + ":0";

    public static final String aeronDirName = Utils.getAeronDir();

    /* ME Cluster Config */
    public static final String meHosts = Utils.getMeClusterAddresses();
    public static final String[] meNodes = meHosts.split(",");

    public static final int meClusterPortBase = Utils.getMeClusterBasePort();

    public static final String meClusterIngressChannel = "aeron:udp";
    public static final String meClusterClientEgressChannel = aeronWildcardPortChannel;
}
