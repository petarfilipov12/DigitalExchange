package digital.exchange.egress.hub.me.egress.poller.cluster.client.fsm;

@FunctionalInterface
public interface ClusterNewLeaderHandler {
    void onNewLeader(int leaderId);
}
