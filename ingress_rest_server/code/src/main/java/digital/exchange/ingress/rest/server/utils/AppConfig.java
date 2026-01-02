package digital.exchange.ingress.rest.server.utils;

public class AppConfig
{
    public static final int portBase = Utils.getBasePort();
    public static final String meHosts = Utils.getClusterAddresses();
    public static final String hostname = Utils.getHostname();

    public static final String aeronDirName = Utils.getAeronDir();

    public static final String aeronWildcardPortChannel = "aeron:udp?endpoint=" + hostname + ":0";

    public static final String meClusterIngressChannel = "aeron:udp";
    public static final String meClusterClientEgressChannel = aeronWildcardPortChannel;
}
