package digital.exchange.me.app.events;

import digital.exchange.me.app.order.Order;

public class EventOrderFill extends Event{
    private final Order takerOrder;
    private final Order makerOrder;
    private final String base;
    private final String quote;
    private final long fillPrice;
    private final long fillQty;

    public EventOrderFill(
            final Order takerOrder,
            final Order makerOrder,
            final String base,
            final String quote,
            final long fillPrice,
            final long fillQty
    )
    {
        super(EventId.EVENT_ID_ORDER_FILL);

        this.takerOrder = takerOrder;
        this.makerOrder = makerOrder;
        this.base = base;
        this.quote = quote;
        this.fillPrice = fillPrice;
        this.fillQty = fillQty;
    }

    public Order getTakerOrder()
    {
        return takerOrder;
    }

    public Order getMakerOrder()
    {
        return makerOrder;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public long getFillPrice()
    {
        return fillPrice;
    }

    public long getFillQty()
    {
        return fillQty;
    }

    @Override
    public String toString()
    {
        String s = super.toString();
        s += ", base: " + base;
        s += ", quote: " + quote;
        s += ", fillPrice: " + fillPrice;
        s += ", fillQty: " + fillQty;
        s += ", takerOrder: " + takerOrder;
        s += ", makerOrder: " + makerOrder;

        return s;
    }
}
