package digital.exchange.egress.hub.cluster.archive.adapter.cluster;

import digital.exchange.egress.hub.cluster.archive.adapter.app.AppLogic;
import digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick.MeMessageGateway;
import digital.exchange.egress.hub.cluster.archive.adapter.utils.MsgPrinter;
import digital.exchange.sbe.hub.inner.MeMessageDecoder;
import digital.exchange.sbe.hub.inner.MessageHeaderDecoder;
import digital.exchange.sbe.hub.inner.PositionUpdateDecoder;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateway
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    private final AppLogic appLogic;
    private final MeMessageGateway meMessageGateway;

    private final MsgPrinter msgPrinter = new MsgPrinter("Gateway");

    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

    private final MeMessageDecoder meMessageDecoder = new MeMessageDecoder();
    private final PositionUpdateDecoder positionUpdateDecoder = new PositionUpdateDecoder();

    public Gateway(final AppLogic appLogic, final MeMessageGateway meMessageGateway) {
        this.appLogic = appLogic;
        this.meMessageGateway = meMessageGateway;
    }

    public void route(final DirectBuffer buffer, final int offset, final int length)
    {
        if(length < MessageHeaderDecoder.ENCODED_LENGTH)
        {
            LOGGER.error("Message too short, ignored.");
            return;
        }
        messageHeaderDecoder.wrap(buffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case MeMessageDecoder.TEMPLATE_ID -> routeMeMessage(buffer, offset);
            case PositionUpdateDecoder.TEMPLATE_ID -> routePositionUpdate(buffer, offset);

            default -> LOGGER.error("Unknown message template {}, ignored.", messageHeaderDecoder.templateId());
        }
    }

    private void routeMeMessage(final DirectBuffer buffer, final int offset)
    {
        meMessageDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        final long position = meMessageDecoder.position();

        LOGGER.info("Position: {}", position);

        final int messageOffset = meMessageDecoder.limit() + MeMessageDecoder.messageBytesHeaderLength();
        final int messageLength = meMessageDecoder.messageBytesLength();

//        meMessageDecoder.getMessageBytes(this.buffer, 0, messageLength);

        appLogic.onMeMessage(buffer, messageOffset, messageLength, position);

        meMessageGateway.route(buffer, messageOffset, messageLength, position);

        msgPrinter.printMsg(buffer, messageOffset);

        //meMessageDecoder.skipMessageBytes(); //For future
    }

    private void routePositionUpdate(final DirectBuffer buffer, final int offset)
    {
        positionUpdateDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        appLogic.onPositionUpdate(positionUpdateDecoder.hubServiceId(), positionUpdateDecoder.position());
    }
}
