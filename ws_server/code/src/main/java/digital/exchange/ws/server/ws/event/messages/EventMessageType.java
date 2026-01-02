package digital.exchange.ws.server.ws.event.messages;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EventMessageType
{
    TRADES,
    ORDER_BOOK;

    @JsonCreator
    public static EventMessageType fromValue(final String str)
    {
        return EventMessageType.valueOf(str);
    }
}
