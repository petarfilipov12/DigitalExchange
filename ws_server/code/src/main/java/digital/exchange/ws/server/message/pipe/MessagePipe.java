package digital.exchange.ws.server.message.pipe;

import java.nio.ByteBuffer;

public interface MessagePipe
{
    boolean write(ByteBuffer buffer, int offset, int length);

    int read(MessagePipeHandler handler, int limit);
}
