package digital.exchange.me.utils;

import digital.exchange.sbe.me.output.*;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.order.OrderSide;
import digital.exchange.me.app.order.OrderType;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbeMeOutUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SbeMeOutUtils.class);

    private static final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private static final OrderFillEventDecoder orderFillEventDecoder = new OrderFillEventDecoder();
    private static final MakerOrderCanceledEventDecoder makerOrderCanceledEventDecoder = new MakerOrderCanceledEventDecoder();
    private static final MakerOrderAddedEventDecoder makerOrderAddedEventDecoder = new MakerOrderAddedEventDecoder();

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

    public static long getEventSeqNum(final DirectBuffer buffer, final int offset, final int length)
    {
        long seqNum = -1;
        if (length < MessageHeaderDecoder.ENCODED_LENGTH)
        {
            LOGGER.error("Message too short, ignored.");
            return -1;
        }
        headerDecoder.wrap(buffer, offset);

        switch (headerDecoder.templateId())
        {
            case OrderFillEventDecoder.TEMPLATE_ID -> seqNum = getOrderFillEventSeqNum(buffer, offset);
            case MakerOrderCanceledEventDecoder.TEMPLATE_ID -> seqNum = getMakerOrderCanceledEventSeqNum(buffer, offset);
            case MakerOrderAddedEventDecoder.TEMPLATE_ID -> seqNum = getMakerOrderAddedEventSeqNum(buffer, offset);

            default -> LOGGER.error("Unknown message template {}, ignored.", headerDecoder.templateId());
        }

        return seqNum;
    }

    private static long getOrderFillEventSeqNum(final DirectBuffer buffer, final int offset)
    {
        orderFillEventDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
        return orderFillEventDecoder.seqNum();
    }

    private static long getMakerOrderCanceledEventSeqNum(final DirectBuffer buffer, final int offset)
    {
        makerOrderCanceledEventDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
        return makerOrderCanceledEventDecoder.seqNum();
    }

    private static long getMakerOrderAddedEventSeqNum(final DirectBuffer buffer, final int offset)
    {
        makerOrderAddedEventDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
        return makerOrderAddedEventDecoder.seqNum();
    }
}
