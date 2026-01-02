package digital.exchange.rest.server.marketdata.app.endpoints.orderbook;

public record OrderBookRequest(String base, String quote, int depth)
{}
