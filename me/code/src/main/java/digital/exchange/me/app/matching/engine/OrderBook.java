package digital.exchange.me.app.matching.engine;

import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import org.agrona.collections.Long2ObjectHashMap;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

//TODO Use FastUtil Map and LinkedHashSet - RESEARCH
//TODO Redo - use lazy cancel + timer cleaning during snapshot? RESEARCH if its worth it
//TODO Think of a way to get SortedMap with getFirst - O(1)
public class OrderBook
{
    private final Long2ObjectHashMap<Order> orders = new Long2ObjectHashMap<>();
    private final TreeMap<Long, LinkedHashSet<Long> > bidPriceMap = new TreeMap<>(Comparator.reverseOrder());
    private final TreeMap<Long, LinkedHashSet<Long> > askPriceMap = new TreeMap<>();

    public ReturnEnum addOrderBid(final Order order)
    {
        return addOrder(bidPriceMap, order);
    }

    public ReturnEnum addOrderAsk(final Order order)
    {
        return addOrder(askPriceMap, order);
    }

    public Order cancelOrder(final long orderId)
    {
        final Order order = orders.remove(orderId);
        if(null == order)
        {
            //Order id does not exist
            return null;
        }

        var priceMap = (order.getOrderSide() == OrderSide.ORDER_SIDE_BUY) ? bidPriceMap : askPriceMap;

        priceMap.computeIfPresent(order.getPrice(), (price, priceLevelSet) -> {
            priceLevelSet.remove(orderId);
            return priceLevelSet.isEmpty() ? null : priceLevelSet; // Return null if the set is empty, so it's removed from the map
        });

        return order;
    }

    public boolean orderExists(final long orderId)
    {
        return orders.containsKey(orderId);
    }

    public boolean orderExists(final Order order)
    {
        return orders.containsKey(order.getOrderId());
    }

    public Order getBestBid()
    {
        return getBest(bidPriceMap);
    }

    public Order getBestAsk()
    {
        return getBest(askPriceMap);
    }

    public Order popFirstBid()
    {
        return popFirst(bidPriceMap);
    }

    public Order popFirstAsk()
    {
        return popFirst(askPriceMap);
    }

    private ReturnEnum addOrder(final TreeMap<Long, LinkedHashSet<Long> > priceMap, final Order order)
    {
        if(order.isOrderInvalid())
        {
            return ReturnEnum.ORDER_INVALID;
        }
        else if(null != orders.putIfAbsent(order.getOrderId(), order))
        {
            return ReturnEnum.ORDER_EXISTS;
        }

        priceMap.computeIfAbsent(order.getPrice(), k -> new LinkedHashSet<Long>()).addLast(order.getOrderId());

        return ReturnEnum.ORDER_ADDED_TO_ORDERBOOK;
    }

    private Order getBest(final TreeMap<Long, LinkedHashSet<Long> > priceMap)
    {
        Order order = null;

        var firstPriceLevel = getFirstPriceLevel(priceMap);
        if(firstPriceLevel != null)
        {
            order = orders.get(firstPriceLevel.getValue().getFirst());
        }

        return order;
    }

    private Order popFirst(final TreeMap<Long, LinkedHashSet<Long> > priceMap)
    {
        Order order = null;

        var firstPriceLevel = getFirstPriceLevel(priceMap);
        if(firstPriceLevel != null)
        {
            order = orders.get(firstPriceLevel.getValue().getFirst());

            firstPriceLevel.getValue().removeFirst();
            if(firstPriceLevel.getValue().isEmpty())
            {
                priceMap.remove(firstPriceLevel.getKey());
            }

            orders.remove(order.getOrderId());
        }

        return order;
    }

    private Map.Entry<Long, LinkedHashSet<Long> > getFirstPriceLevel(final TreeMap<Long, LinkedHashSet<Long> > priceMap)
    {
        Map.Entry<Long, LinkedHashSet<Long> > priceLevel = null;

        var it = priceMap.entrySet().iterator();

        while(it.hasNext())
        {
            var entry = it.next();

            if(!entry.getValue().isEmpty())
            {
                priceLevel = entry;
                break;
            }
            else
            {
                it.remove();
            }
        }

        return priceLevel;
    }
}
