package digital.exchange.ws.server.cluster.client;

import digital.exchange.ws.server.message.pipe.MessagePipe;
import io.aeron.cluster.client.EgressListener;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

import java.nio.ByteBuffer;

public class MeEgressListener implements EgressListener
{
    private final MessagePipe messagePipe;

    public MeEgressListener(final MessagePipe messagePipe)
    {
        this.messagePipe = messagePipe;
    }

    @Override
    public void onMessage(
            final long clusterSessionId,
            final long timestamp,
            final DirectBuffer buffer,
            final int offset,
            final int length,
            final Header header
    )
    {
        ByteBuffer bb = buffer.byteBuffer();

        if(bb == null)
        {
            bb = ByteBuffer.wrap(buffer.byteArray());
        }

        messagePipe.write(bb, offset, length);
    }
}
