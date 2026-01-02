package digital.exchange.me.utils;

import digital.exchange.sbe.me.input.*;

import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbeMeInUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SbeMeInUtils.class);

    public static OrderSide convertSbeOrderSideToNativeOrderSide(SbeOrderSide sbeOrderSide)
    {
        OrderSide orderSide;

        switch(sbeOrderSide)
        {
            case SbeOrderSide.ORDER_SIDE_BUY -> orderSide = OrderSide.ORDER_SIDE_BUY;
            case SbeOrderSide.ORDER_SIDE_SELL -> orderSide = OrderSide.ORDER_SIDE_SELL;
            default -> orderSide = OrderSide.ORDER_SIDE_INVALID;
        }

        return orderSide;
    }

    public static SbeOrderSide covertNativeOrderSideToSbeOrderSide(OrderSide orderSide)
    {
        SbeOrderSide sbeOrderSide;

        switch(orderSide)
        {
            case OrderSide.ORDER_SIDE_BUY -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_BUY;
            case OrderSide.ORDER_SIDE_SELL -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_SELL;
            default -> sbeOrderSide = SbeOrderSide.ORDER_SIDE_INVALID;
        }

        return sbeOrderSide;
    }

    public static OrderType convertSbeOrderTypeToNativeOrderType(SbeOrderType sbeOrderType)
    {
        OrderType orderType;

        switch(sbeOrderType)
        {
            case SbeOrderType.ORDER_TYPE_LIMIT -> orderType = OrderType.ORDER_TYPE_LIMIT;
            case SbeOrderType.ORDER_TYPE_MARKET -> orderType = OrderType.ORDER_TYPE_MARKET;
            default -> orderType = OrderType.ORDER_TYPE_INVALID;
        }

        return orderType;
    }

    public static SbeOrderType covertNativeOrderTypeToSbeOrderType(OrderType orderType)
    {
        SbeOrderType sbeOrderType;

        switch(orderType)
        {
            case OrderType.ORDER_TYPE_LIMIT -> sbeOrderType = SbeOrderType.ORDER_TYPE_LIMIT;
            case OrderType.ORDER_TYPE_MARKET -> sbeOrderType = SbeOrderType.ORDER_TYPE_MARKET;
            default -> sbeOrderType = SbeOrderType.ORDER_TYPE_INVALID;
        }

        return sbeOrderType;
    }

    public static void convertOrderToSbeOrderEncoder(final OrderEncoder orderEncoder, final Order order)
    {
        orderEncoder.orderId(order.getOrderId());
        orderEncoder.accountId(order.getAccountId());

        orderEncoder.orderSide(covertNativeOrderSideToSbeOrderSide(order.getOrderSide()));
        orderEncoder.orderType(covertNativeOrderTypeToSbeOrderType(order.getOrderType()));

        orderEncoder.price(order.getPrice());
        orderEncoder.qty(order.getQty());
        orderEncoder.filled(order.getFilled());
        orderEncoder.base(order.getBase());
        orderEncoder.quote(order.getQuote());
    }

    public static ResponseEnum convertReturnEnumToSbeResponseEnum(final ReturnEnum result)
    {
        final ResponseEnum resp;

        switch (result)
        {
            case ReturnEnum.OK -> resp = ResponseEnum.OK;
            case ReturnEnum.NOT_OK -> resp = ResponseEnum.NOT_OK;
            case ReturnEnum.ORDER_INVALID -> resp = ResponseEnum.ORDER_INVALID;
            case ReturnEnum.ORDER_FULLY_FILLED -> resp = ResponseEnum.ORDER_FULLY_FILLED;
            case ReturnEnum.ORDER_ADDED_TO_ORDERBOOK -> resp = ResponseEnum.ORDER_ADDED_TO_ORDERBOOK;
            case ReturnEnum.ORDER_REJECTED -> resp = ResponseEnum.ORDER_REJECTED;
            case ReturnEnum.ORDERBOOK_EMPTY -> resp = ResponseEnum.ORDERBOOK_EMPTY;
            case ReturnEnum.ORDER_EXISTS -> resp = ResponseEnum.ORDER_EXISTS;
            case ReturnEnum.ORDER_NOT_EXIST -> resp = ResponseEnum.ORDER_NOT_EXIST;
            case ReturnEnum.ORDER_CANCELED -> resp = ResponseEnum.ORDER_CANCELED;
            case ReturnEnum.MATCHING_ENGINE_NOT_EXIST -> resp = ResponseEnum.MATCHING_ENGINE_NOT_EXIST;
            case ReturnEnum.MATCHING_ENGINE_EXISTS -> resp = ResponseEnum.MATCHING_ENGINE_EXISTS;
            case ReturnEnum.ACCOUNT_EXISTS -> resp = ResponseEnum.ACCOUNT_EXISTS;
            case ReturnEnum.ACCOUNT_NOT_EXIST -> resp = ResponseEnum.ACCOUNT_NOT_EXIST;
            case ReturnEnum.ACCOUNT_SYMBOL_EXISTS -> resp = ResponseEnum.ACCOUNT_SYMBOL_EXISTS;
            case ReturnEnum.ACCOUNT_SYMBOL_NOT_EXIST -> resp = ResponseEnum.ACCOUNT_SYMBOL_NOT_EXIST;
            case ReturnEnum.ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS -> resp = ResponseEnum.ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS;

            default -> resp = ResponseEnum.NOT_OK;
        }

        return resp;
    }
}
