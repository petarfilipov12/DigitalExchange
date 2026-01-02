/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.input;

import org.agrona.DirectBuffer;


/**
 * Order description
 */
@SuppressWarnings("all")
public final class OrderDecoder
{
    public static final int SCHEMA_ID = 101;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "1";
    public static final int ENCODED_LENGTH = 56;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public OrderDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public DirectBuffer buffer()
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

    public static int orderIdSinceVersion()
    {
        return 0;
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

    public long orderId()
    {
        return buffer.getLong(offset + 0, BYTE_ORDER);
    }


    public static int accountIdEncodingOffset()
    {
        return 8;
    }

    public static int accountIdEncodingLength()
    {
        return 8;
    }

    public static int accountIdSinceVersion()
    {
        return 0;
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

    public long accountId()
    {
        return buffer.getLong(offset + 8, BYTE_ORDER);
    }


    public static int baseEncodingOffset()
    {
        return 16;
    }

    public static int baseEncodingLength()
    {
        return 4;
    }

    public static int baseSinceVersion()
    {
        return 0;
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


    public static int quoteEncodingOffset()
    {
        return 20;
    }

    public static int quoteEncodingLength()
    {
        return 4;
    }

    public static int quoteSinceVersion()
    {
        return 0;
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


    public static int priceEncodingOffset()
    {
        return 24;
    }

    public static int priceEncodingLength()
    {
        return 8;
    }

    public static int priceSinceVersion()
    {
        return 0;
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

    public long price()
    {
        return buffer.getLong(offset + 24, BYTE_ORDER);
    }


    public static int qtyEncodingOffset()
    {
        return 32;
    }

    public static int qtyEncodingLength()
    {
        return 8;
    }

    public static int qtySinceVersion()
    {
        return 0;
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

    public long qty()
    {
        return buffer.getLong(offset + 32, BYTE_ORDER);
    }


    public static int filledEncodingOffset()
    {
        return 40;
    }

    public static int filledEncodingLength()
    {
        return 8;
    }

    public static int filledSinceVersion()
    {
        return 0;
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

    public long filled()
    {
        return buffer.getLong(offset + 40, BYTE_ORDER);
    }


    public static int orderSideEncodingOffset()
    {
        return 48;
    }

    public static int orderSideEncodingLength()
    {
        return 4;
    }

    public static int orderSideSinceVersion()
    {
        return 0;
    }

    public int orderSideRaw()
    {
        return buffer.getInt(offset + 48, BYTE_ORDER);
    }

    public SbeOrderSide orderSide()
    {
        return SbeOrderSide.get(buffer.getInt(offset + 48, BYTE_ORDER));
    }


    public static int orderTypeEncodingOffset()
    {
        return 52;
    }

    public static int orderTypeEncodingLength()
    {
        return 4;
    }

    public static int orderTypeSinceVersion()
    {
        return 0;
    }

    public int orderTypeRaw()
    {
        return buffer.getInt(offset + 52, BYTE_ORDER);
    }

    public SbeOrderType orderType()
    {
        return SbeOrderType.get(buffer.getInt(offset + 52, BYTE_ORDER));
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

        builder.append('(');
        builder.append("orderId=");
        builder.append(this.orderId());
        builder.append('|');
        builder.append("accountId=");
        builder.append(this.accountId());
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
        builder.append("price=");
        builder.append(this.price());
        builder.append('|');
        builder.append("qty=");
        builder.append(this.qty());
        builder.append('|');
        builder.append("filled=");
        builder.append(this.filled());
        builder.append('|');
        builder.append("orderSide=");
        builder.append(this.orderSide());
        builder.append('|');
        builder.append("orderType=");
        builder.append(this.orderType());
        builder.append(')');

        return builder;
    }
}
