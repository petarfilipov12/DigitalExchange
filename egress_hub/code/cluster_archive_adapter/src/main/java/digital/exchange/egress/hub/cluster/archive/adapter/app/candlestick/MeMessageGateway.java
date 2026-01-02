package digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick;

import digital.exchange.sbe.me.output.*;
import org.agrona.DirectBuffer;

public class MeMessageGateway
{
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

    private final ClockUpdateEventDecoder clockUpdateEventDecoder = new ClockUpdateEventDecoder();

    private final AddedPairMatchingEngineEventDecoder addedPairMatchingEngineEventDecoder =
            new AddedPairMatchingEngineEventDecoder();
    private final RemovedPairMatchingEngineEventDecoder removedPairMatchingEngineEventDecoder =
            new RemovedPairMatchingEngineEventDecoder();

    private final OrderFillEventDecoder orderFillEventDecoder = new OrderFillEventDecoder();

    private final CandlestickUpdaters candlestickUpdaters;

    private long position = 0;

    public MeMessageGateway(final CandlestickUpdaters candlestickUpdaters) {
        this.candlestickUpdaters = candlestickUpdaters;
    }

    public void route(DirectBuffer buffer, final int offset, final int length, final long position)
    {
        if(position <= this.position)
        {
            return;
        }

        this.position = position;

        messageHeaderDecoder.wrap(buffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case ClockUpdateEventDecoder.TEMPLATE_ID -> routeClockUpdateEvent(buffer, offset);

            case AddedPairMatchingEngineEventDecoder.TEMPLATE_ID -> routeAddedPairEvent(buffer, offset);
            case RemovedPairMatchingEngineEventDecoder.TEMPLATE_ID -> routeRemovedPairEvent(buffer, offset);

            case OrderFillEventDecoder.TEMPLATE_ID -> routeOrderFillEvent(buffer, offset);
        }
    }

    private void routeClockUpdateEvent(final DirectBuffer buffer, final int offset)
    {
        clockUpdateEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        candlestickUpdaters.clockUpdate(clockUpdateEventDecoder.timestamp());
    }

    private void routeAddedPairEvent(final DirectBuffer buffer, final int offset)
    {
        addedPairMatchingEngineEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        candlestickUpdaters.addPair(
                addedPairMatchingEngineEventDecoder.timestamp(),
                addedPairMatchingEngineEventDecoder.base(),
                addedPairMatchingEngineEventDecoder.quote()
        );
    }

    private void routeRemovedPairEvent(final DirectBuffer buffer, final int offset)
    {
        removedPairMatchingEngineEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        candlestickUpdaters.removePair(
                removedPairMatchingEngineEventDecoder.timestamp(),
                removedPairMatchingEngineEventDecoder.base(),
                removedPairMatchingEngineEventDecoder.quote()
        );
    }

    private void routeOrderFillEvent(final DirectBuffer buffer, final int offset)
    {
        orderFillEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        candlestickUpdaters.orderFill(
                orderFillEventDecoder.timestamp(),
                orderFillEventDecoder.base(),
                orderFillEventDecoder.quote(),
                orderFillEventDecoder.fillPrice(),
                orderFillEventDecoder.fillQty()
        );
    }
}
