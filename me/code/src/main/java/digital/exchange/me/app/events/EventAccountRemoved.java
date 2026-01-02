package digital.exchange.me.app.events;

public class EventAccountRemoved extends Event
{
    private final long accountId;

    public EventAccountRemoved(final long accountId)
    {
        super(EventId.EVENT_ID_ACCOUNT_REMOVED);

        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", accountId: " + accountId;
    }
}
