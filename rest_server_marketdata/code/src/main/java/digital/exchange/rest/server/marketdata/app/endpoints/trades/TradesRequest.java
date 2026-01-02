package digital.exchange.rest.server.marketdata.app.endpoints.trades;

public record TradesRequest(String base, String quote, int limit)
{}
