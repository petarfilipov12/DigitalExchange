package digital.exchange.egress.hub.cluster.archive.adapter.app;

import org.agrona.DirectBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MessageMappedBuffer
{
    private final MappedByteBuffer buffer;

    public MessageMappedBuffer(final String fileLocation, final int bufferLength)
    {
        Path path = Path.of(fileLocation);

        try
        {
            Files.createDirectories(path.getParent());

            try(FileChannel fileChannel = FileChannel.open(path,
                    StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE))
            {
                buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bufferLength);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMsgToMappedBuffer(DirectBuffer bufferSrc, final int offset, final int length)
    {
        System.out.print("offset: " + offset + ", length: " + length);

        if(length > buffer.capacity())
        {
            throw new RuntimeException("No space in mappedBuffer, length: " + length);
        }

        if(bufferSrc.byteArray() != null)
        {
            buffer.put(0, bufferSrc.byteArray(), offset, length);
        }
        else if(bufferSrc.byteBuffer() != null)
        {
            buffer.put(0, bufferSrc.byteBuffer(), offset, length);
        }
        else
        {
            throw new RuntimeException("No BytBuffer or byteArray in DirectBuffer");
        }

        buffer.force(0, length);
    }

}
