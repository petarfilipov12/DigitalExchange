package digital.exchange.ingress.rest.server.utils;


import digital.exchange.sbe.me.input.*;
import digital.exchange.ingress.rest.server.app.Order;

public class SbeUtils
{
    public static String convertSbeOrderSideToString(final SbeOrderSide sbeOrderSide)
    {
        return sbeOrderSide.name();
    }

    public static SbeOrderSide covertStringToSbeOrderSide(final String orderSide)
    {
        final SbeOrderSide sbeOrderSide;

        switch(orderSide)
        {
            case "BUY" -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_BUY;
            case "SELL" -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_SELL;
            default -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_INVALID;
        }

        return sbeOrderSide;
    }

    public static String convertSbeOrderTypeToString(final SbeOrderType sbeOrderType)
    {
        return sbeOrderType.name();
    }

    public static SbeOrderType covertStringToSbeOrderType(final String orderType)
    {
        SbeOrderType sbeOrderType;

        switch(orderType)
        {
            case "LIMIT" -> sbeOrderType = SbeOrderType.ORDER_TYPE_LIMIT;
            case "MARKET" -> sbeOrderType = SbeOrderType.ORDER_TYPE_MARKET;
            default -> sbeOrderType = SbeOrderType.ORDER_TYPE_INVALID;
        }

        return sbeOrderType;
    }

    public static Order convertSbeOrderToNativeOrder(final OrderDecoder orderDecoder)
    {
        return new Order(
            orderDecoder.orderId(),
            convertSbeOrderSideToString(orderDecoder.orderSide()),
            convertSbeOrderTypeToString(orderDecoder.orderType()),
            orderDecoder.price(),
            orderDecoder.qty(),
            orderDecoder.filled()
        );
    }

    public static String convertSbeResponseEnumToString(final ResponseEnum response)
    {
        return response.name();
    }
}
