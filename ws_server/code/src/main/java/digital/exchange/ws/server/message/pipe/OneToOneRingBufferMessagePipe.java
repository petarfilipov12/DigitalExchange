package digital.exchange.ws.server.message.pipe;

import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import java.nio.ByteBuffer;

public class OneToOneRingBufferMessagePipe implements MessagePipe
{
    private final int bufferLength;

    private final OneToOneRingBuffer ringBuffer;

    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(0, 0);

    public OneToOneRingBufferMessagePipe(final int capacity)
    {
        bufferLength = capacity + RingBufferDescriptor.TRAILER_LENGTH;

        final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength));
        ringBuffer = new OneToOneRingBuffer(buffer);
    }

    @Override
    public boolean write(final ByteBuffer buffer, final int offset, final int length)
    {
        unsafeBuffer.wrap(buffer);
        return ringBuffer.write(1, unsafeBuffer, offset, length);
    }

    @Override
    public int read(final MessagePipeHandler handler, final int limit)
    {
        return ringBuffer.read((msgTypeId, buffer, offset, length) ->
                handler.onMessage(buffer.byteBuffer(), offset, length),
                limit
        );
    }
}
