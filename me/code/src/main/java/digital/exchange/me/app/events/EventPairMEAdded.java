package digital.exchange.me.app.events;

public class EventPairMEAdded extends Event
{
    private final String base;
    private final String quote;

    public EventPairMEAdded(final String base, final String quote)
    {
        super(EventId.EVENT_ID_ME_PAIR_ADDED);

        this.base = base;
        this.quote = quote;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    @Override
    public String toString()
    {
        String s = super.toString();
        s += ", base: " + base;
        s += ", quote: " + quote;

        return s;
    }
}
