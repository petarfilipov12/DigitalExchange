/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.hub.inner;

import org.agrona.DirectBuffer;


/**
 * Signal From Hub Node
 */
@SuppressWarnings("all")
public final class SignalEnumFromHubNodeDecoder
{
    public static final int BLOCK_LENGTH = 16;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 103;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final SignalEnumFromHubNodeDecoder parentMessage = this;
    private DirectBuffer buffer;
    private int offset;
    private int limit;
    int actingBlockLength;
    int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public SignalEnumFromHubNodeDecoder wrap(
        final DirectBuffer buffer,
        final int offset,
        final int actingBlockLength,
        final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public SignalEnumFromHubNodeDecoder wrapAndApplyHeader(
        final DirectBuffer buffer,
        final int offset,
        final MessageHeaderDecoder headerDecoder)
    {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId)
        {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
            buffer,
            offset + MessageHeaderDecoder.ENCODED_LENGTH,
            headerDecoder.blockLength(),
            headerDecoder.version());
    }

    public SignalEnumFromHubNodeDecoder sbeRewind()
    {
        return wrap(buffer, offset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        return decodedLength;
    }

    public int actingVersion()
    {
        return actingVersion;
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int hubServiceIdMaskId()
    {
        return 1;
    }

    public static int hubServiceIdMaskSinceVersion()
    {
        return 0;
    }

    public static int hubServiceIdMaskEncodingOffset()
    {
        return 0;
    }

    public static int hubServiceIdMaskEncodingLength()
    {
        return 4;
    }

    public static String hubServiceIdMaskMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int hubServiceIdMaskNullValue()
    {
        return -2147483648;
    }

    public static int hubServiceIdMaskMinValue()
    {
        return -2147483647;
    }

    public static int hubServiceIdMaskMaxValue()
    {
        return 2147483647;
    }

    public int hubServiceIdMask()
    {
        return buffer.getInt(offset + 0, BYTE_ORDER);
    }


    public static int positionId()
    {
        return 4;
    }

    public static int positionSinceVersion()
    {
        return 0;
    }

    public static int positionEncodingOffset()
    {
        return 4;
    }

    public static int positionEncodingLength()
    {
        return 8;
    }

    public static String positionMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long positionNullValue()
    {
        return -9223372036854775808L;
    }

    public static long positionMinValue()
    {
        return -9223372036854775807L;
    }

    public static long positionMaxValue()
    {
        return 9223372036854775807L;
    }

    public long position()
    {
        return buffer.getLong(offset + 4, BYTE_ORDER);
    }


    public static int signalId()
    {
        return 3;
    }

    public static int signalSinceVersion()
    {
        return 0;
    }

    public static int signalEncodingOffset()
    {
        return 12;
    }

    public static int signalEncodingLength()
    {
        return 4;
    }

    public static String signalMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public int signalRaw()
    {
        return buffer.getInt(offset + 12, BYTE_ORDER);
    }

    public SignalEnum signal()
    {
        return SignalEnum.get(buffer.getInt(offset + 12, BYTE_ORDER));
    }


    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final SignalEnumFromHubNodeDecoder decoder = new SignalEnumFromHubNodeDecoder();
        decoder.wrap(buffer, offset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final int originalLimit = limit();
        limit(offset + actingBlockLength);
        builder.append("[SignalEnumFromHubNode](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("hubServiceIdMask=");
        builder.append(this.hubServiceIdMask());
        builder.append('|');
        builder.append("position=");
        builder.append(this.position());
        builder.append('|');
        builder.append("signal=");
        builder.append(this.signal());

        limit(originalLimit);

        return builder;
    }
    
    public SignalEnumFromHubNodeDecoder sbeSkip()
    {
        sbeRewind();

        return this;
    }
}
