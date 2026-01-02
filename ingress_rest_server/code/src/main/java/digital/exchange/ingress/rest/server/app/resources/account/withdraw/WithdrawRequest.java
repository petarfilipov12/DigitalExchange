package digital.exchange.ingress.rest.server.app.resources.account.withdraw;

public record WithdrawRequest(long accountId, String symbol, long qty)
{}
