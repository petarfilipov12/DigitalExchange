package digital.exchange.egress.hub.me.egress.poller.hub;

import digital.exchange.egress.hub.me.egress.poller.utils.AppConfig;
import digital.exchange.sbe.hub.inner.SignalEnumFromHubNodeDecoder;
import digital.exchange.sbe.hub.inner.MessageHeaderDecoder;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignalDecoder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalDecoder.class);

    private static final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private static final SignalEnumFromHubNodeDecoder signalDecoder = new SignalEnumFromHubNodeDecoder();

    public static SignalEnumFromHubNodeDecoder decodeSignal(DirectBuffer buffer, int offset, int length)
    {
        if(length < SignalEnumFromHubNodeDecoder.BLOCK_LENGTH)
        {
            LOGGER.info("Message length too small. skipping");
            return null;
        }

        signalDecoder.wrapAndApplyHeader(buffer, offset, messageHeaderDecoder);

        if(messageHeaderDecoder.templateId() != SignalEnumFromHubNodeDecoder.TEMPLATE_ID)
        {
            LOGGER.info("Not a command message. skipping");
            return null;
        }

        if((signalDecoder.hubServiceIdMask() & AppConfig.serviceId) == 0)
        {
            return null;
        }

        return signalDecoder;
    }
}
