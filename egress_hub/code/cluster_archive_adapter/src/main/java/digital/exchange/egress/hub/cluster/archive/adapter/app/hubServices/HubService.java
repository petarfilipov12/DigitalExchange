package digital.exchange.egress.hub.cluster.archive.adapter.app.hubServices;

public class HubService
{
    private final int hubServiceId;
    private long position;

    public HubService(final int hubServiceId, final long position) {
        this.hubServiceId = hubServiceId;
        this.position = position;
    }

    public int getHubServiceId() {
        return hubServiceId;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(final long position) {
        this.position = position;
    }
}
