/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.output;

import org.agrona.MutableDirectBuffer;


/**
 * Order description
 */
@SuppressWarnings("all")
public final class OrderEncoder
{
    public static final int SCHEMA_ID = 102;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final int ENCODED_LENGTH = 56;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public OrderEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public MutableDirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public static int orderIdEncodingOffset()
    {
        return 0;
    }

    public static int orderIdEncodingLength()
    {
        return 8;
    }

    public static long orderIdNullValue()
    {
        return -9223372036854775808L;
    }

    public static long orderIdMinValue()
    {
        return -9223372036854775807L;
    }

    public static long orderIdMaxValue()
    {
        return 9223372036854775807L;
    }

    public OrderEncoder orderId(final long value)
    {
        buffer.putLong(offset + 0, value, BYTE_ORDER);
        return this;
    }


    public static int accountIdEncodingOffset()
    {
        return 8;
    }

    public static int accountIdEncodingLength()
    {
        return 8;
    }

    public static long accountIdNullValue()
    {
        return -9223372036854775808L;
    }

    public static long accountIdMinValue()
    {
        return -9223372036854775807L;
    }

    public static long accountIdMaxValue()
    {
        return 9223372036854775807L;
    }

    public OrderEncoder accountId(final long value)
    {
        buffer.putLong(offset + 8, value, BYTE_ORDER);
        return this;
    }


    public static int baseEncodingOffset()
    {
        return 16;
    }

    public static int baseEncodingLength()
    {
        return 4;
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


    public OrderEncoder base(final int index, final byte value)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 16 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }
    public OrderEncoder putBase(final byte value0, final byte value1, final byte value2, final byte value3)
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

    public OrderEncoder putBase(final byte[] src, final int srcOffset)
    {
        final int length = 4;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 16, src, srcOffset, length);

        return this;
    }

    public OrderEncoder base(final String src)
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

    public OrderEncoder base(final CharSequence src)
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

    public static int quoteEncodingOffset()
    {
        return 20;
    }

    public static int quoteEncodingLength()
    {
        return 4;
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


    public OrderEncoder quote(final int index, final byte value)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 20 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }
    public OrderEncoder putQuote(final byte value0, final byte value1, final byte value2, final byte value3)
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

    public OrderEncoder putQuote(final byte[] src, final int srcOffset)
    {
        final int length = 4;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 20, src, srcOffset, length);

        return this;
    }

    public OrderEncoder quote(final String src)
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

    public OrderEncoder quote(final CharSequence src)
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

    public static int priceEncodingOffset()
    {
        return 24;
    }

    public static int priceEncodingLength()
    {
        return 8;
    }

    public static long priceNullValue()
    {
        return -9223372036854775808L;
    }

    public static long priceMinValue()
    {
        return -9223372036854775807L;
    }

    public static long priceMaxValue()
    {
        return 9223372036854775807L;
    }

    public OrderEncoder price(final long value)
    {
        buffer.putLong(offset + 24, value, BYTE_ORDER);
        return this;
    }


    public static int qtyEncodingOffset()
    {
        return 32;
    }

    public static int qtyEncodingLength()
    {
        return 8;
    }

    public static long qtyNullValue()
    {
        return -9223372036854775808L;
    }

    public static long qtyMinValue()
    {
        return -9223372036854775807L;
    }

    public static long qtyMaxValue()
    {
        return 9223372036854775807L;
    }

    public OrderEncoder qty(final long value)
    {
        buffer.putLong(offset + 32, value, BYTE_ORDER);
        return this;
    }


    public static int filledEncodingOffset()
    {
        return 40;
    }

    public static int filledEncodingLength()
    {
        return 8;
    }

    public static long filledNullValue()
    {
        return -9223372036854775808L;
    }

    public static long filledMinValue()
    {
        return -9223372036854775807L;
    }

    public static long filledMaxValue()
    {
        return 9223372036854775807L;
    }

    public OrderEncoder filled(final long value)
    {
        buffer.putLong(offset + 40, value, BYTE_ORDER);
        return this;
    }


    public static int orderSideEncodingOffset()
    {
        return 48;
    }

    public static int orderSideEncodingLength()
    {
        return 4;
    }

    public OrderEncoder orderSide(final SbeOrderSide value)
    {
        buffer.putInt(offset + 48, value.value(), BYTE_ORDER);
        return this;
    }

    public static int orderTypeEncodingOffset()
    {
        return 52;
    }

    public static int orderTypeEncodingLength()
    {
        return 4;
    }

    public OrderEncoder orderType(final SbeOrderType value)
    {
        buffer.putInt(offset + 52, value.value(), BYTE_ORDER);
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

        final OrderDecoder decoder = new OrderDecoder();
        decoder.wrap(buffer, offset);

        return decoder.appendTo(builder);
    }
}
