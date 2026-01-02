/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.hub.inner;

@SuppressWarnings("all")
public enum SignalEnum
{
    CONNECT(0),

    DISCONNECT(1),

    DO_WORK(2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    SignalEnum(final int value)
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
    public static SignalEnum get(final int value)
    {
        switch (value)
        {
            case 0: return CONNECT;
            case 1: return DISCONNECT;
            case 2: return DO_WORK;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
