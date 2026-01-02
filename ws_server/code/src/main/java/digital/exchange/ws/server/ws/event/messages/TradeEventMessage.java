package digital.exchange.ws.server.ws.event.messages;

public class TradeEventMessage extends EventMessage
{
    public TradeEventMessage(final String symbol, final long timestamp, final long price, final long qty)
    {
        super(EventMessageType.TRADES, symbol, timestamp);

        jsonString = '{' +
                "\"type\": \"trade\", " +
                "\"symbol\": \"" + symbol + "\", " +
                "\"timestamp\": \"" + timestamp + "\", " +
                "\"price\": \"" + price + "\", " +
                "\"qty\": \"" + qty + "\"" +
                '}';
    }
}
