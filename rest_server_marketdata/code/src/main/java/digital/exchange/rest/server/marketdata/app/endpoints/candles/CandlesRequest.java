package digital.exchange.rest.server.marketdata.app.endpoints.candles;

public record CandlesRequest(String base, String quote, String interval, int limit)
{}
