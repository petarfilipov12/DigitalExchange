package digital.exchange.me.app.order;

public class Order
{
    private final long orderId;
    private final long accountId;
    private final OrderSide orderSide;
    private final OrderType orderType;
    private final long price;
    private final long qty;
    private long filled;
    private final String base;
    private final String quote;

    public Order(
            final long orderId,
            final long accountId,
            final OrderSide orderSide,
            final OrderType orderType,
            final long price,
            final long qty,
            final long filled,
            final String base,
            final String quote
    )
    {
        this.orderId = orderId;
        this.accountId = accountId;
        this.orderSide = orderSide;
        this.orderType = orderType;
        this.price = price;
        this.qty = qty;
        this.filled = filled;
        this.base = base;
        this.quote = quote;
    }

    public long getOrderId()
    {
        return orderId;
    }

    public long getAccountId()
    {
        return accountId;
    }

    public OrderSide getOrderSide()
    {
        return orderSide;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public long getPrice()
    {
        return price;
    }

    public long getQty()
    {
        return qty;
    }

    public long getFilled()
    {
        return filled;
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }

    public void setFilled(final long filled)
    {
        this.filled = filled;
    }

    public boolean isFullyFilled()
    {
        return (filled >= qty);
    }

    public boolean isOrderInvalid()
    {
        return (orderSide == OrderSide.ORDER_SIDE_INVALID) || (orderType == OrderType.ORDER_TYPE_INVALID);
    }

    public long fillOrder(final long fillQty)
    {
        filled += fillQty;

        return filled;
    }

    @Override
    public String toString() {
        String s =  "orderId: " + orderId;
        s += ", accountId: " + accountId;
        s += ", orderSide: " + orderSide;
        s += ", orderType: " + orderType;
        s += ", price: " + price;
        s += ", qty: " + qty;
        s += ", filled: " + filled;

        return s;
    }
}
