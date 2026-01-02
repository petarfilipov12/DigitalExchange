/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.input;

@SuppressWarnings("all")
public enum SbeOrderType
{
    ORDER_TYPE_MARKET(0),

    ORDER_TYPE_LIMIT(1),

    ORDER_TYPE_INVALID(2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    SbeOrderType(final int value)
    {
        this.value = value;
    }

    /**
     * The raw encoded value in the Java type representation.
     *
     * @return the raw value encoded.
     */
    public int value()
    {
        return value;
    }

    /**
     * Lookup the enum value representing the value.
     *
     * @param value encoded to be looked up.
     * @return the enum value representing the value.
     */
    public static SbeOrderType get(final int value)
    {
        switch (value)
        {
            case 0: return ORDER_TYPE_MARKET;
            case 1: return ORDER_TYPE_LIMIT;
            case 2: return ORDER_TYPE_INVALID;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
