/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.output;

import org.agrona.MutableDirectBuffer;


/**
 * Order Fill Event
 */
@SuppressWarnings("all")
public final class OrderFillEventEncoder
{
    public static final int BLOCK_LENGTH = 152;
    public static final int TEMPLATE_ID = 6;
    public static final int SCHEMA_ID = 102;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderFillEventEncoder parentMessage = this;
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

    public OrderFillEventEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public OrderFillEventEncoder wrapAndApplyHeader(
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

    public OrderFillEventEncoder seqNum(final long value)
    {
        buffer.putLong(offset + 0, value, BYTE_ORDER);
        return this;
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

    public OrderFillEventEncoder timestamp(final long value)
    {
        buffer.putLong(offset + 8, value, BYTE_ORDER);
        return this;
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


    public OrderFillEventEncoder base(final int index, final byte value)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 16 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }
    public OrderFillEventEncoder putBase(final byte value0, final byte value1, final byte value2, final byte value3)
    {
        buffer.putByte(offset + 16, value0);
        buffer.putByte(offset + 17, value1);
        buffer.putByte(offset + 18, value2);
        buffer.putByte(offset + 19, value3);

        return this;
    }

    public static String baseCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public OrderFillEventEncoder putBase(final byte[] src, final int srcOffset)
    {
        final int length = 4;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 16, src, srcOffset, length);

        return this;
    }

    public OrderFillEventEncoder base(final String src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 16, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 16 + start, (byte)0);
        }

        return this;
    }

    public OrderFillEventEncoder base(final CharSequence src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 16, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 16 + start, (byte)0);
        }

        return this;
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


    public OrderFillEventEncoder quote(final int index, final byte value)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 20 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }
    public OrderFillEventEncoder putQuote(final byte value0, final byte value1, final byte value2, final byte value3)
    {
        buffer.putByte(offset + 20, value0);
        buffer.putByte(offset + 21, value1);
        buffer.putByte(offset + 22, value2);
        buffer.putByte(offset + 23, value3);

        return this;
    }

    public static String quoteCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public OrderFillEventEncoder putQuote(final byte[] src, final int srcOffset)
    {
        final int length = 4;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 20, src, srcOffset, length);

        return this;
    }

    public OrderFillEventEncoder quote(final String src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 20, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 20 + start, (byte)0);
        }

        return this;
    }

    public OrderFillEventEncoder quote(final CharSequence src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 20, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 20 + start, (byte)0);
        }

        return this;
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

    public OrderFillEventEncoder fillPrice(final long value)
    {
        buffer.putLong(offset + 24, value, BYTE_ORDER);
        return this;
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

    public OrderFillEventEncoder fillQty(final long value)
    {
        buffer.putLong(offset + 32, value, BYTE_ORDER);
        return this;
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

    private final OrderEncoder takerOrder = new OrderEncoder();

    public OrderEncoder takerOrder()
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

    private final OrderEncoder makerOrder = new OrderEncoder();

    public OrderEncoder makerOrder()
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

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final OrderFillEventDecoder decoder = new OrderFillEventDecoder();
        decoder.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }
}
