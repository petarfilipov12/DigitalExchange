package digital.exchange.egress.hub.cluster.archive.adapter.utils;

import digital.exchange.sbe.me.output.*;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbeUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SbeUtils.class);

    private static final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private static final OrderFillEventDecoder orderFillEventDecoder = new OrderFillEventDecoder();
    private static final MakerOrderCanceledEventDecoder makerOrderCanceledEventDecoder = new MakerOrderCanceledEventDecoder();
    private static final MakerOrderAddedEventDecoder makerOrderAddedEventDecoder = new MakerOrderAddedEventDecoder();

    public static String convertSbeOrderToString(OrderDecoder orderDecoder)
    {
        final long orderId = orderDecoder.orderId();

        final String orderSide;
        switch(orderDecoder.orderSide())
        {
            case SbeOrderSide.ORDER_SIDE_BUY -> orderSide = "ORDER_SIDE_BUY";
            case SbeOrderSide.ORDER_SIDE_SELL -> orderSide = "ORDER_SIDE_SELL";
            default -> orderSide = "ORDER_SIDE_INVALID";
        }

        final String orderType;
        switch(orderDecoder.orderType())
        {
            case SbeOrderType.ORDER_TYPE_MARKET -> orderType = "ORDER_TYPE_MARKET";
            case SbeOrderType.ORDER_TYPE_LIMIT -> orderType = "ORDER_TYPE_LIMIT";
            default -> orderType = "ORDER_TYPE_INVALID";
        }

        final long price = orderDecoder.price();
        final long qty = orderDecoder.qty();
        final long filled = orderDecoder.filled();

        final String base = orderDecoder.base();
        final String quote = orderDecoder.quote();

        return String.format("Order: orderId %d, orderSide %s, orderType %s, price %d, qty %d, filled %d, base %s, quote %s",
                orderId, orderSide, orderType, price, qty, filled, base, quote);
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