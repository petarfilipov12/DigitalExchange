package digital.exchange.ws.server.cluster.client.fsm;

@FunctionalInterface
public interface ClusterClientFsmStateChangeListener
{
    void onConnected(ClusterClientFsmAgent clusterClientFsmAgent);

    default void onDisconnected(ClusterClientFsmAgent clusterClientFsmAgent)
    {}
}
