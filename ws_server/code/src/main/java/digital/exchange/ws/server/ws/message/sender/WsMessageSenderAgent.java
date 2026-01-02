package digital.exchange.ws.server.ws.message.sender;

import digital.exchange.sbe.me.output.MessageHeaderDecoder;
import digital.exchange.ws.server.message.pipe.MessagePipe;
import digital.exchange.ws.server.message.pipe.MessagePipeHandler;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class WsMessageSenderAgent implements Agent
{
    private final MessagePipe messagePipe;
    private final MessagePipeHandler handler;

    public WsMessageSenderAgent(final MessagePipe messagePipe, final MessagePipeHandler handler) {
        this.messagePipe = messagePipe;
        this.handler = handler;
    }

    @Override
    public int doWork()
    {
        return messagePipe.read(handler, 1);
    }

    @Override
    public String roleName()
    {
        return "WsMessageSender";
    }
}
