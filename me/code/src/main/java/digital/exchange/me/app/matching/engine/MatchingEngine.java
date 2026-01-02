package digital.exchange.me.app.matching.engine;

import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.app.event.emitter.EventEmitter;
import digital.exchange.me.app.events.EventOrderFill;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;

import java.util.function.BiFunction;
import java.util.function.Function;

//TODO Add market order logic
public class MatchingEngine
{
    private final String base;
    private final String quote;

    private final OrderBook orderBook = new OrderBook();
    private final EventEmitter eventEmitter;

    public MatchingEngine(final String base, final String quote, EventEmitter eventEmitter)
    {
        this.base = base;
        this.quote = quote;

        this.eventEmitter = eventEmitter;
    }

    public OrderReturn cancelOrder(final long orderId)
    {
        System.out.println("CANCEL ORDER");
        System.out.println(orderId);

        var order = orderBook.cancelOrder(orderId);

        if(null == order)
        {
            return new OrderReturn(ReturnEnum.ORDER_NOT_EXIST, null);
        }

        return new OrderReturn(ReturnEnum.ORDER_CANCELED, order);
    }

    public ReturnEnum addOrder(final Order order)
    {
        System.out.println("ADD ORDER");
        System.out.println(order.toString());

        if(order.isOrderInvalid())
        {
            return ReturnEnum.ORDER_INVALID;
        }

        if(orderBook.orderExists(order))
        {
            return ReturnEnum.ORDER_EXISTS;
        }

        return matchOrder(order);
    }

    private ReturnEnum matchOrder(final Order order)
    {
        ReturnEnum ret = ReturnEnum.NOT_OK;
        do
        {
            if(order.isFullyFilled())
            {
                return ReturnEnum.ORDER_FULLY_FILLED;
            }

            ret = matchCycle(order);
        }
        while(ret == ReturnEnum.OK);

        return ret;
    }

    private ReturnEnum matchCycle(final Order order)
    {
        final Order bookOrder;
        final Runnable popFunc;
        final Function<Order, ReturnEnum> addOrderFunc;
        final BiFunction<Long, Long, Boolean> priceCheckFunc;

        if(OrderSide.ORDER_SIDE_BUY == order.getOrderSide())
        {
            bookOrder = orderBook.getBestAsk();
            addOrderFunc = orderBook::addOrderBid;
            priceCheckFunc = (bookOrderPrice, orderPrice) -> bookOrderPrice > orderPrice;
            popFunc = orderBook::popFirstAsk;
        }
        else if(OrderSide.ORDER_SIDE_SELL == order.getOrderSide())
        {
            bookOrder = orderBook.getBestBid();
            addOrderFunc = orderBook::addOrderAsk;
            priceCheckFunc = (bookOrderPrice, orderPrice) -> bookOrderPrice < orderPrice;
            popFunc = orderBook::popFirstBid;
        }
        else
        {
            return ReturnEnum.ORDER_INVALID;
        }

        if(OrderType.ORDER_TYPE_LIMIT == order.getOrderType())
        {
            if( (null == bookOrder) || priceCheckFunc.apply(bookOrder.getPrice(), order.getPrice()) )
            {
                return addOrderFunc.apply(order);
            }

            if(order.getAccountId() == bookOrder.getAccountId())
            {
                return ReturnEnum.ORDER_REJECTED;
            }
        }
        else if(OrderType.ORDER_TYPE_MARKET == order.getOrderType())
        {
            if(null == bookOrder)
            {
                return ReturnEnum.ORDERBOOK_EMPTY;
            }

            if(order.getAccountId() == bookOrder.getAccountId())
            {
                return ReturnEnum.ORDER_REJECTED;
            }
        }
        else
        {
            return ReturnEnum.ORDER_INVALID;
        }

        final long fill_price = bookOrder.getPrice();
        final long fill_qty = Math.min(bookOrder.getQty(), order.getQty());

        order.fillOrder(fill_qty);
        bookOrder.fillOrder(fill_qty);

        eventEmitter.emitEvent(new EventOrderFill(order, bookOrder, base, quote, fill_price, fill_qty));

        if(bookOrder.isFullyFilled())
        {
            popFunc.run();
        }

        return ReturnEnum.OK;
    }
}
