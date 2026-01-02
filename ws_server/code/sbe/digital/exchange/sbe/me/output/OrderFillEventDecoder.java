/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.output;

import org.agrona.DirectBuffer;


/**
 * Order Fill Event
 */
@SuppressWarnings("all")
public final class OrderFillEventDecoder
{
    public static final int BLOCK_LENGTH = 152;
    public static final int TEMPLATE_ID = 6;
    public static final int SCHEMA_ID = 102;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderFillEventDecoder parentMessage = this;
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

    public OrderFillEventDecoder wrap(
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

    public OrderFillEventDecoder wrapAndApplyHeader(
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

    public OrderFillEventDecoder sbeRewind()
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

    public static int seqNumId()
    {
        return 1;
    }

    public static int seqNumSinceVersion()
    {
        return 0;
    }

    public static int seqNumEncodingOffset()
    {
        return 0;
    }

    public static int seqNumEncodingLength()
    {
        return 8;
    }

    public static String seqNumMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long seqNumNullValue()
    {
        return -9223372036854775808L;
    }

    public static long seqNumMinValue()
    {
        return -9223372036854775807L;
    }

    public static long seqNumMaxValue()
    {
        return 9223372036854775807L;
    }

    public long seqNum()
    {
        return buffer.getLong(offset + 0, BYTE_ORDER);
    }


    public static int timestampId()
    {
        return 2;
    }

    public static int timestampSinceVersion()
    {
        return 0;
    }

    public static int timestampEncodingOffset()
    {
        return 8;
    }

    public static int timestampEncodingLength()
    {
        return 8;
    }

    public static String timestampMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long timestampNullValue()
    {
        return -9223372036854775808L;
    }

    public static long timestampMinValue()
    {
        return -9223372036854775807L;
    }

    public static long timestampMaxValue()
    {
        return 9223372036854775807L;
    }

    public long timestamp()
    {
        return buffer.getLong(offset + 8, BYTE_ORDER);
    }


    public static int baseId()
    {
        return 3;
    }

    public static int baseSinceVersion()
    {
        return 0;
    }

    public static int baseEncodingOffset()
    {
        return 16;
    }

    public static int baseEncodingLength()
    {
        return 4;
    }

    public static String baseMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static byte baseNullValue()
    {
        return (byte)0;
    }

    public static byte baseMinValue()
    {
        return (byte)32;
    }

    public static byte baseMaxValue()
    {
        return (byte)126;
    }

    public static int baseLength()
    {
        return 4;
    }


    public byte base(final int index)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 16 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String baseCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public int getBase(final byte[] dst, final int dstOffset)
    {
        final int length = 4;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(offset + 16, dst, dstOffset, length);

        return length;
    }

    public String base()
    {
        final byte[] dst = new byte[4];
        buffer.getBytes(offset + 16, dst, 0, 4);

        int end = 0;
        for (; end < 4 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public int getBase(final Appendable value)
    {
        for (int i = 0; i < 4; ++i)
        {
            final int c = buffer.getByte(offset + 16 + i) & 0xFF;
            if (c == 0)
            {
                return i;
            }

            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException ex)
            {
                throw new java.io.UncheckedIOException(ex);
            }
        }

        return 4;
    }


    public static int quoteId()
    {
        return 4;
    }

    public static int quoteSinceVersion()
    {
        return 0;
    }

    public static int quoteEncodingOffset()
    {
        return 20;
    }

    public static int quoteEncodingLength()
    {
        return 4;
    }

    public static String quoteMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static byte quoteNullValue()
    {
        return (byte)0;
    }

    public static byte quoteMinValue()
    {
        return (byte)32;
    }

    public static byte quoteMaxValue()
    {
        return (byte)126;
    }

    public static int quoteLength()
    {
        return 4;
    }


    public byte quote(final int index)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 20 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String quoteCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public int getQuote(final byte[] dst, final int dstOffset)
    {
        final int length = 4;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(offset + 20, dst, dstOffset, length);

        return length;
    }

    public String quote()
    {
        final byte[] dst = new byte[4];
        buffer.getBytes(offset + 20, dst, 0, 4);

        int end = 0;
        for (; end < 4 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public int getQuote(final Appendable value)
    {
        for (int i = 0; i < 4; ++i)
        {
            final int c = buffer.getByte(offset + 20 + i) & 0xFF;
            if (c == 0)
            {
                return i;
            }

            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException ex)
            {
                throw new java.io.UncheckedIOException(ex);
            }
        }

        return 4;
    }


    public static int fillPriceId()
    {
        return 5;
    }

    public static int fillPriceSinceVersion()
    {
        return 0;
    }

    public static int fillPriceEncodingOffset()
    {
        return 24;
    }

    public static int fillPriceEncodingLength()
    {
        return 8;
    }

    public static String fillPriceMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long fillPriceNullValue()
    {
        return -9223372036854775808L;
    }

    public static long fillPriceMinValue()
    {
        return -9223372036854775807L;
    }

    public static long fillPriceMaxValue()
    {
        return 9223372036854775807L;
    }

    public long fillPrice()
    {
        return buffer.getLong(offset + 24, BYTE_ORDER);
    }


    public static int fillQtyId()
    {
        return 6;
    }

    public static int fillQtySinceVersion()
    {
        return 0;
    }

    public static int fillQtyEncodingOffset()
    {
        return 32;
    }

    public static int fillQtyEncodingLength()
    {
        return 8;
    }

    public static String fillQtyMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long fillQtyNullValue()
    {
        return -9223372036854775808L;
    }

    public static long fillQtyMinValue()
    {
        return -9223372036854775807L;
    }

    public static long fillQtyMaxValue()
    {
        return 9223372036854775807L;
    }

    public long fillQty()
    {
        return buffer.getLong(offset + 32, BYTE_ORDER);
    }


    public static int takerOrderId()
    {
        return 7;
    }

    public static int takerOrderSinceVersion()
    {
        return 0;
    }

    public static int takerOrderEncodingOffset()
    {
        return 40;
    }

    public static int takerOrderEncodingLength()
    {
        return 56;
    }

    public static String takerOrderMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private final OrderDecoder takerOrder = new OrderDecoder();

    public OrderDecoder takerOrder()
    {
        takerOrder.wrap(buffer, offset + 40);
        return takerOrder;
    }

    public static int makerOrderId()
    {
        return 8;
    }

    public static int makerOrderSinceVersion()
    {
        return 0;
    }

    public static int makerOrderEncodingOffset()
    {
        return 96;
    }

    public static int makerOrderEncodingLength()
    {
        return 56;
    }

    public static String makerOrderMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private final OrderDecoder makerOrder = new OrderDecoder();

    public OrderDecoder makerOrder()
    {
        makerOrder.wrap(buffer, offset + 96);
        return makerOrder;
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final OrderFillEventDecoder decoder = new OrderFillEventDecoder();
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
        builder.append("[OrderFillEvent](sbeTemplateId=");
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
        builder.append("seqNum=");
        builder.append(this.seqNum());
        builder.append('|');
        builder.append("timestamp=");
        builder.append(this.timestamp());
        builder.append('|');
        builder.append("base=");
        for (int i = 0; i < baseLength() && this.base(i) > 0; i++)
        {
            builder.append((char)this.base(i));
        }
        builder.append('|');
        builder.append("quote=");
        for (int i = 0; i < quoteLength() && this.quote(i) > 0; i++)
        {
            builder.append((char)this.quote(i));
        }
        builder.append('|');
        builder.append("fillPrice=");
        builder.append(this.fillPrice());
        builder.append('|');
        builder.append("fillQty=");
        builder.append(this.fillQty());
        builder.append('|');
        builder.append("takerOrder=");
        final OrderDecoder takerOrder = this.takerOrder();
        if (null != takerOrder)
        {
            takerOrder.appendTo(builder);
        }
        else
        {
            builder.append("null");
        }
        builder.append('|');
        builder.append("makerOrder=");
        final OrderDecoder makerOrder = this.makerOrder();
        if (null != makerOrder)
        {
            makerOrder.appendTo(builder);
        }
        else
        {
            builder.append("null");
        }

        limit(originalLimit);

        return builder;
    }
    
    public OrderFillEventDecoder sbeSkip()
    {
        sbeRewind();

        return this;
    }
}
