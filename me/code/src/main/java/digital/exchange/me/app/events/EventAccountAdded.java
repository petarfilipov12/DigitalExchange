package digital.exchange.me.app.events;

public class EventAccountAdded extends Event
{
    private final long accountId;

    public EventAccountAdded(final long accountId)
    {
        super(EventId.EVENT_ID_ACCOUNT_ADDED);

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
