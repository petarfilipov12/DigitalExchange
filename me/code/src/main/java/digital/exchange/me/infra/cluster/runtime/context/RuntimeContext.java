package digital.exchange.me.infra.cluster.runtime.context;

import io.aeron.cluster.service.Cluster;

public class RuntimeContext
{
    private static Cluster cluster;
    private static Cluster.Role clusterRole;
    private static long timestamp;
    private static long seqNum = 0;
    private static final SessionContext sessionContext = new SessionContext();
    private static final SubscribedSessions subscribedSessions = new SubscribedSessions();

    public static Cluster getCluster() {
        return cluster;
    }

    public static void setCluster(final Cluster cluster) {
        RuntimeContext.cluster = cluster;
    }

    public static Cluster.Role getClusterRole() {
        return clusterRole;
    }

    public static void setClusterRole(final Cluster.Role clusterRole) {
        RuntimeContext.clusterRole = clusterRole;
    }

    public static long getSeqNum() {
        return seqNum;
    }

    public static long getAndIncSeqNum() {
        final long value = seqNum;
        seqNum++;

        return value;
    }

    public static void setSeqNum(long seqNum) {
        RuntimeContext.seqNum = seqNum;
    }

    public static void setClusterTime(final long timestamp)
    {
        RuntimeContext.timestamp = timestamp;
    }

    public static long getClusterTime()
    {
        return timestamp;
    }

    public static SessionContext getSessionContext()
    {
        return sessionContext;
    }

    public static SubscribedSessions getSubscribedSessions()
    {
        return subscribedSessions;
    }

}
