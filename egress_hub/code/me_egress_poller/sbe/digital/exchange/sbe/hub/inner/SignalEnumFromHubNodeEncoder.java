/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.hub.inner;

import org.agrona.MutableDirectBuffer;


/**
 * Signal From Hub Node
 */
@SuppressWarnings("all")
public final class SignalEnumFromHubNodeEncoder
{
    public static final int BLOCK_LENGTH = 16;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 103;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final SignalEnumFromHubNodeEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
    private int offset;
    private int limit;

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

    public MutableDirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public SignalEnumFromHubNodeEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public SignalEnumFromHubNodeEncoder wrapAndApplyHeader(
        final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
    {
        headerEncoder
            .wrap(buffer, offset)
            .blockLength(BLOCK_LENGTH)
            .templateId(TEMPLATE_ID)
            .schemaId(SCHEMA_ID)
            .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
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

    public SignalEnumFromHubNodeEncoder hubServiceIdMask(final int value)
    {
        buffer.putInt(offset + 0, value, BYTE_ORDER);
        return this;
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

    public SignalEnumFromHubNodeEncoder position(final long value)
    {
        buffer.putLong(offset + 4, value, BYTE_ORDER);
        return this;
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

    public SignalEnumFromHubNodeEncoder signal(final SignalEnum value)
    {
        buffer.putInt(offset + 12, value.value(), BYTE_ORDER);
        return this;
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final SignalEnumFromHubNodeDecoder decoder = new SignalEnumFromHubNodeDecoder();
        decoder.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }
}
