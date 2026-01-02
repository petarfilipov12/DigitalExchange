package digital.exchange.ingress.rest.server.aeron.infra.egress;

import digital.exchange.sbe.me.input.*;
import digital.exchange.ingress.rest.server.utils.SbeUtils;
import digital.exchange.ingress.rest.server.app.response.Response;
import digital.exchange.ingress.rest.server.app.response.ResponseOrder;
import digital.exchange.ingress.rest.server.app.resources.RequestsHandler;
import org.agrona.DirectBuffer;

public class EgressMsgHandler
{

    private final CommandResponseDecoder commandResponseDecoder = new CommandResponseDecoder();
    private final CommandResponseOrderDecoder commandResponseOrderDecoder = new CommandResponseOrderDecoder();

    public void handleMsg(DirectBuffer buffer, int offset)
    {
        final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

        messageHeaderDecoder.wrap(buffer, offset);

        switch (messageHeaderDecoder.templateId())
        {
            case CommandResponseDecoder.TEMPLATE_ID -> {
                handleResp(messageHeaderDecoder, buffer, offset);
            }
            case CommandResponseOrderDecoder.TEMPLATE_ID -> {
                handleRespOrder(messageHeaderDecoder, buffer, offset);
            }

            default -> System.out.printf("UNKNOWN MSG: %d\n", messageHeaderDecoder.templateId());
        }
    }

    private void handleResp(final MessageHeaderDecoder messageHeaderDecoder, final DirectBuffer buffer, final int offset)
    {
        commandResponseDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        Response resp = new Response(SbeUtils.convertSbeResponseEnumToString(commandResponseDecoder.response()));

        RequestsHandler.completeRequest(commandResponseDecoder.seqNum(), resp);
    }

    private void handleRespOrder(final MessageHeaderDecoder messageHeaderDecoder, final DirectBuffer buffer, final int offset)
    {
        commandResponseOrderDecoder.wrap(
                buffer,
                offset + MessageHeaderDecoder.ENCODED_LENGTH,
                messageHeaderDecoder.blockLength(),
                messageHeaderDecoder.version()
        );

        ResponseOrder resp = new ResponseOrder(
                SbeUtils.convertSbeResponseEnumToString(commandResponseOrderDecoder.response()),
                SbeUtils.convertSbeOrderToNativeOrder(commandResponseOrderDecoder.order())
        );

        RequestsHandler.completeRequest(commandResponseOrderDecoder.seqNum(), resp);
    }
}
