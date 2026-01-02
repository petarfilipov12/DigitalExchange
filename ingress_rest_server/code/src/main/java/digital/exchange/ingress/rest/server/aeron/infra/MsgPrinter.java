package digital.exchange.ingress.rest.server.aeron.infra;

import digital.exchange.sbe.me.input.*;
import digital.exchange.ingress.rest.server.utils.SbeUtils;
import org.agrona.DirectBuffer;

public class MsgPrinter
{
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

    private final CommandResponseDecoder commandResponseDecoder = new CommandResponseDecoder();
    private final CommandResponseOrderDecoder commandResponseOrderDecoder = new CommandResponseOrderDecoder();

    public void printMsg(DirectBuffer buffer,
                         int offset)
    {
        messageHeaderDecoder.wrap(buffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case CommandResponseDecoder.TEMPLATE_ID -> {
                commandResponseDecoder.wrapAndApplyHeader(buffer, offset, messageHeaderDecoder);
                printResp(
                        commandResponseDecoder.response(),
                        commandResponseDecoder.seqNum()
                );
            }
            case CommandResponseOrderDecoder.TEMPLATE_ID -> {
                commandResponseOrderDecoder.wrapAndApplyHeader(buffer, offset, messageHeaderDecoder);
                printResp(
                        commandResponseOrderDecoder.response(),
                        commandResponseOrderDecoder.order(),
                        commandResponseOrderDecoder.seqNum()
                );
            }

            default -> System.out.printf("UNKNOWN MSG: %d\n", messageHeaderDecoder.templateId());
        }
    }

    private void printResp(ResponseEnum response, long seqNum)
    {
        System.out.println("SeqNum: " + seqNum);
        System.out.println("ADD ORDER RESP: " + SbeUtils.convertSbeResponseEnumToString(response));
    }

    private void printResp(ResponseEnum response, OrderDecoder orderDecoder, long seqNum)
    {
        System.out.println("SeqNum: " + seqNum);
        System.out.println("ADD ORDER RESP: " + SbeUtils.convertSbeResponseEnumToString(response));

        displayOrder(orderDecoder);
    }

    private void displayOrder(OrderDecoder orderDecoder)
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

        var order_s = String.format("Order: orderId %d, orderSide %s, orderType %s, price %d, qty %d, filled %d",
                orderId, orderSide, orderType, price, qty, filled);

        System.out.println(order_s);
    }
}
