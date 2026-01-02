package digital.exchange.me.app.events;

import digital.exchange.me.app.order.Order;

public class EventMakerOrderCanceled extends Event
{
    private final Order makerOrder;

    public EventMakerOrderCanceled(final Order makerOrder)
    {
        super(EventId.EVENT_ID_MARKET_ORDER_CANCELED);

        this.makerOrder = makerOrder;
    }

    public Order getMakerOrder() {
        return makerOrder;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", makerOrder: " + makerOrder;
    }
}
