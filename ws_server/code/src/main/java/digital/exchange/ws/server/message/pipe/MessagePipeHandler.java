package digital.exchange.ws.server.message.pipe;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface MessagePipeHandler
{
    void onMessage(ByteBuffer buffer, int offset, int length);
}
