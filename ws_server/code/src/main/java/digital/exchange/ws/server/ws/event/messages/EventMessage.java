package digital.exchange.ws.server.ws.event.messages;

public abstract class EventMessage
{
    protected final EventMessageType messageType;
    protected final String symbol;
    protected final long timestamp;

    protected String jsonString;

    public EventMessage(final EventMessageType messageType, final String symbol, final long timestamp)
    {
        this.messageType = messageType;
        this.symbol = symbol;
        this.timestamp = timestamp;
    }

    public EventMessageType getMessageType()
    {
        return messageType;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public String getJsonString()
    {
        return jsonString;
    }
}
