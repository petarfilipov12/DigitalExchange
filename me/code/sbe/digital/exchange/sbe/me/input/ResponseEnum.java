/* Generated SBE (Simple Binary Encoding) message codec. */
package digital.exchange.sbe.me.input;

@SuppressWarnings("all")
public enum ResponseEnum
{
    OK(0),

    NOT_OK(1),

    ORDER_INVALID(3),

    ORDER_FULLY_FILLED(4),

    ORDER_ADDED_TO_ORDERBOOK(5),

    ORDER_REJECTED(6),

    ORDERBOOK_EMPTY(7),

    ORDER_EXISTS(8),

    ORDER_NOT_EXIST(9),

    ORDER_CANCELED(10),

    MATCHING_ENGINE_NOT_EXIST(11),

    MATCHING_ENGINE_EXISTS(12),

    ACCOUNT_EXISTS(13),

    ACCOUNT_NOT_EXIST(14),

    ACCOUNT_SYMBOL_EXISTS(15),

    ACCOUNT_SYMBOL_NOT_EXIST(16),

    ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS(17),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(-2147483648);

    private final int value;

    ResponseEnum(final int value)
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
    public static ResponseEnum get(final int value)
    {
        switch (value)
        {
            case 0: return OK;
            case 1: return NOT_OK;
            case 3: return ORDER_INVALID;
            case 4: return ORDER_FULLY_FILLED;
            case 5: return ORDER_ADDED_TO_ORDERBOOK;
            case 6: return ORDER_REJECTED;
            case 7: return ORDERBOOK_EMPTY;
            case 8: return ORDER_EXISTS;
            case 9: return ORDER_NOT_EXIST;
            case 10: return ORDER_CANCELED;
            case 11: return MATCHING_ENGINE_NOT_EXIST;
            case 12: return MATCHING_ENGINE_EXISTS;
            case 13: return ACCOUNT_EXISTS;
            case 14: return ACCOUNT_NOT_EXIST;
            case 15: return ACCOUNT_SYMBOL_EXISTS;
            case 16: return ACCOUNT_SYMBOL_NOT_EXIST;
            case 17: return ACCOUNT_SYMBOL_INSUFFICIENT_FUNDS;
            case -2147483648: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
