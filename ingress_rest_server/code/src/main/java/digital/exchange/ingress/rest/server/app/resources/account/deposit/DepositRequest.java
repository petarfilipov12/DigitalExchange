package digital.exchange.ingress.rest.server.app.resources.account.deposit;

public record DepositRequest(long accountId, String symbol, long qty)
{}
