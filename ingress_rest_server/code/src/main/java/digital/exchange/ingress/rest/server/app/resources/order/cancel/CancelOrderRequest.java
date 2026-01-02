package digital.exchange.ingress.rest.server.app.resources.order.cancel;

public record CancelOrderRequest(String base, String quote, long accountId, long orderId)
{}