package digital.exchange.ingress.rest.server.app.resources.order.add;

public record AddOrderRequest(String base, String quote, long accountId, String orderSide, String orderType, long price, long qty)
{}
