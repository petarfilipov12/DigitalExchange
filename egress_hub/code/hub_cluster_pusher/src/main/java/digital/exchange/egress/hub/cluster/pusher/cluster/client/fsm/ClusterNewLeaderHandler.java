package digital.exchange.egress.hub.cluster.pusher.cluster.client.fsm;

@FunctionalInterface
public interface ClusterNewLeaderHandler {
    void onNewLeader(int leaderId);
}
