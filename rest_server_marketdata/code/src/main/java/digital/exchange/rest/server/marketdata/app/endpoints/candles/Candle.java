package digital.exchange.rest.server.marketdata.app.endpoints.candles;

public record Candle(
        long startTimestamp,
        long endTimestamp,
        long open,
        long high,
        long low,
        long close,
        long volume
)
{
}
