package digital.exchange.me.app.events;

public class EventDeposit extends Event
{
    private final long accountId;
    private final String symbol;
    private final long qty;

    public EventDeposit(final long accountId, final String symbol, final long qty)
    {
        super(EventId.EVENT_ID_DEPOSIT);

        this.accountId = accountId;
        this.symbol = symbol;
        this.qty = qty;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getQty() {
        return qty;
    }

    @Override
    public String toString()
    {
        String s = super.toString();
        s += ", accountId: " + accountId;
        s += ", symbol: " + symbol;
        s += ", qty: " + qty;

        return s;
    }
}
