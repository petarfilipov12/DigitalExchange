/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.output;

@SuppressWarnings("all")
public enum SbeOrderSide
{
    ORDER_SIDE_BUY(0),

    ORDER_SIDE_SELL(1),

    ORDER_SIDE_INVALID(2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    SbeOrderSide(final int value)
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
    public static SbeOrderSide get(final int value)
    {
        switch (value)
        {
            case 0: return ORDER_SIDE_BUY;
            case 1: return ORDER_SIDE_SELL;
            case 2: return ORDER_SIDE_INVALID;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
