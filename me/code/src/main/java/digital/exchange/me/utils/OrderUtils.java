package digital.exchange.me.utils;

import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;

public class OrderUtils
{
    public static String convertOrderSideToString(final OrderSide orderSide)
    {
        final String orderSideString;

        switch (orderSide)
        {
            case OrderSide.ORDER_SIDE_BUY -> orderSideString = "ORDER_SIDE_BUY";
            case OrderSide.ORDER_SIDE_SELL -> orderSideString = "ORDER_SIDE_SELL";
            case OrderSide.ORDER_SIDE_INVALID -> orderSideString = "ORDER_SIDE_INVALID";

            default -> orderSideString = "ORDER_SIDE_UNKNOWN: " + orderSide;
        }

        return orderSideString;
    }

    public static String convertOrderTypeToString(final OrderType orderType)
    {
        final String orderTypeString;

        switch (orderType)
        {
            case OrderType.ORDER_TYPE_MARKET -> orderTypeString = "ORDER_TYPE_MARKET";
            case OrderType.ORDER_TYPE_LIMIT -> orderTypeString = "ORDER_TYPE_LIMIT";
            case OrderType.ORDER_TYPE_INVALID -> orderTypeString = "ORDER_TYPE_INVALID";

            default -> orderTypeString = "ORDER_TYPE_UNKNOWN: " + orderType;
        }

        return orderTypeString;
    }
}
