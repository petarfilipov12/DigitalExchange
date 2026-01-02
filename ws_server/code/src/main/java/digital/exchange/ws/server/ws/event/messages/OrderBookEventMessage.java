package digital.exchange.ws.server.ws.event.messages;

public class OrderBookEventMessage extends EventMessage
{
    public OrderBookEventMessage(final String symbol, final long timestamp, final String side, final long price, final long qtyChange)
    {
        super(EventMessageType.ORDER_BOOK, symbol, timestamp);

        jsonString = '{' +
                "\"type\": \"orderBook\", " +
                "\"symbol\": \"" + symbol + "\", " +
                "\"timestamp\": \"" + timestamp + "\", " +
                "\"side\": \"" + side + "\", " +
                "\"price\": \"" + price + "\", " +
                "\"qtyChange\": \"" + qtyChange + "\"" +
                '}';
    }
}
