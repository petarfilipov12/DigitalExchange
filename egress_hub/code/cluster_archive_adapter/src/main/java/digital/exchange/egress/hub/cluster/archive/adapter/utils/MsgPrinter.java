package digital.exchange.egress.hub.cluster.archive.adapter.utils;



import digital.exchange.sbe.me.output.*;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgPrinter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgPrinter.class);

    private final String prefix;

    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

    private final ClockUpdateEventDecoder clockUpdateEventDecoder = new ClockUpdateEventDecoder();

    private final OrderFillEventDecoder orderFillEventDecoder = new OrderFillEventDecoder();
    private final MakerOrderAddedEventDecoder makerOrderAddedEventDecoder = new MakerOrderAddedEventDecoder();
    private final MakerOrderCanceledEventDecoder makerOrderCanceledEventDecoder = new MakerOrderCanceledEventDecoder();

    public MsgPrinter(String prefix) {
        this.prefix = prefix;
    }

    public void printMsg(DirectBuffer buffer, int offset)
    {
        messageHeaderDecoder.wrap(buffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case ClockUpdateEventDecoder.TEMPLATE_ID -> printClockUpdate(buffer, offset);

            case OrderFillEventDecoder.TEMPLATE_ID -> printOrderFill(buffer, offset);
            case MakerOrderAddedEventDecoder.TEMPLATE_ID -> printMakerOrderAdded(buffer, offset);
            case MakerOrderCanceledEventDecoder.TEMPLATE_ID -> printMakerOrderCanceled(buffer, offset);

            default -> LOGGER.info("{} UNKNOWN MSG: {}", prefix, messageHeaderDecoder.templateId());
        }
    }

    private void printClockUpdate(DirectBuffer buffer, int offset)
    {
        clockUpdateEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        LOGGER.info("{}SeqNum: {} :ClockUpdateEvent-> timestamp: {}",
                prefix,
                clockUpdateEventDecoder.seqNum(),
                clockUpdateEventDecoder.timestamp()
        );
    }

    private void printOrderFill(DirectBuffer buffer, int offset)
    {
        orderFillEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        LOGGER.info("{}SeqNum: {}, timestamp: {} :OrderFillEvent-> base: {}, quote: {}, fillPrice: {}, fillQty:{}, makerOrder: {}, takerOrder: {}",
                prefix,
                orderFillEventDecoder.seqNum(),
                orderFillEventDecoder.timestamp(),
                orderFillEventDecoder.base(),
                orderFillEventDecoder.quote(),
                orderFillEventDecoder.fillPrice(),
                orderFillEventDecoder.fillQty(),
                SbeUtils.convertSbeOrderToString(orderFillEventDecoder.makerOrder()),
                SbeUtils.convertSbeOrderToString(orderFillEventDecoder.takerOrder())
        );
    }

    private void printMakerOrderAdded(DirectBuffer buffer, int offset)
    {
        makerOrderAddedEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        LOGGER.info("{}SeqNum: {}, timestamp: {} :MakerOrderAddedEvent-> makerOrder: {}",
                prefix,
                makerOrderAddedEventDecoder.seqNum(),
                makerOrderAddedEventDecoder.timestamp(),
                SbeUtils.convertSbeOrderToString(makerOrderAddedEventDecoder.makerOrder())
        );
    }

    private void printMakerOrderCanceled(DirectBuffer buffer, int offset)
    {
        makerOrderCanceledEventDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        LOGGER.info("{}SeqNum: {}, timestamp: {} :MakerOrderCanceled-> makerOrder: {}",
                prefix,
                makerOrderCanceledEventDecoder.seqNum(),
                makerOrderCanceledEventDecoder.timestamp(),
                SbeUtils.convertSbeOrderToString(makerOrderCanceledEventDecoder.makerOrder())
        );
    }
}
