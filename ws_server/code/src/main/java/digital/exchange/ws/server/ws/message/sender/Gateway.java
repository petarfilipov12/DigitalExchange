package digital.exchange.ws.server.ws.message.sender;

import digital.exchange.sbe.me.output.*;
import digital.exchange.ws.server.ws.WsServer;
import digital.exchange.ws.server.ws.event.messages.OrderBookEventMessage;
import digital.exchange.ws.server.ws.event.messages.TradeEventMessage;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class Gateway
{
    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(0, 0);
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private final MakerOrderAddedEventDecoder makerOrderAddedEventDecoder = new MakerOrderAddedEventDecoder();
    private final MakerOrderCanceledEventDecoder makerOrderCanceledEventDecoder = new MakerOrderCanceledEventDecoder();
    private final OrderFillEventDecoder orderFillEventDecoder = new OrderFillEventDecoder();

    private final WsServer wsServer;

    public Gateway(final WsServer wsServer) {
        this.wsServer = wsServer;
    }

    public void decode(final ByteBuffer buffer, final int offset, final int length)
    {
        if(length < MessageHeaderDecoder.ENCODED_LENGTH)
        {
            return ;
        }

        unsafeBuffer.wrap(buffer);
        messageHeaderDecoder.wrap(unsafeBuffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case MakerOrderAddedEventDecoder.TEMPLATE_ID -> decodeMakerOrderAdded(unsafeBuffer, offset);
            case MakerOrderCanceledEventDecoder.TEMPLATE_ID -> decodeMakerOrderCanceled(unsafeBuffer, offset);
            case OrderFillEventDecoder.TEMPLATE_ID -> decodeOrderFillEvent(unsafeBuffer, offset);
        }
    }

    private void decodeMakerOrderAdded(final DirectBuffer buffer, final int offset)
    {
        makerOrderAddedEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        final OrderDecoder orderDecoder = makerOrderAddedEventDecoder.makerOrder();
        final long qty = orderDecoder.qty() - orderDecoder.filled();

        if(qty <= 0)
        {
            return;
        }

        final String symbol = orderDecoder.base().toUpperCase() + orderDecoder.quote().toUpperCase();

        wsServer.sendEventMessage(new OrderBookEventMessage(
                symbol,
                makerOrderAddedEventDecoder.timestamp(),
                orderDecoder.orderSide().name(),
                orderDecoder.price(),
                qty
        ));
    }

    private void decodeMakerOrderCanceled(final DirectBuffer buffer, final int offset)
    {
        makerOrderCanceledEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        final OrderDecoder orderDecoder = makerOrderCanceledEventDecoder.makerOrder();
        final long qty = orderDecoder.qty() - orderDecoder.filled();

        if(qty <= 0)
        {
            return;
        }

        final String symbol = orderDecoder.base().toUpperCase() + orderDecoder.quote().toUpperCase();

        wsServer.sendEventMessage(new OrderBookEventMessage(
                symbol,
                makerOrderCanceledEventDecoder.timestamp(),
                orderDecoder.orderSide().name(),
                orderDecoder.price(),
                -1 * qty
        ));
    }

    private void decodeOrderFillEvent(final DirectBuffer buffer, final int offset)
    {
        orderFillEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        final OrderDecoder orderDecoder = orderFillEventDecoder.makerOrder();

        final String symbol = orderFillEventDecoder.base().toUpperCase() + orderFillEventDecoder.quote().toUpperCase();
        final long timestamp = orderFillEventDecoder.timestamp();
        final long price = orderFillEventDecoder.fillPrice();
        final long qty = orderFillEventDecoder.fillQty();

        wsServer.sendEventMessage(new TradeEventMessage(
                symbol,
                timestamp,
                price,
                qty
        ));

        wsServer.sendEventMessage(new OrderBookEventMessage(
                symbol,
                timestamp,
                orderDecoder.orderSide().name(),
                price,
                -1 * qty
        ));
    }
}
