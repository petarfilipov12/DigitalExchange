package digital.exchange.ingress.rest.server.app;

public record Order(long id, String orderSide, String orderType, long price, long qty, long filled)
{}
