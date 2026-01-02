package digital.exchange.me.app.account;

import java.util.HashMap;

public class Account
{
    private final long accountId;
    private final HashMap<String, SymbolAmount> symbolAmountHashMap = new HashMap<>();

    public Account(long accountId) {
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }

    public SymbolAmount getSymbolAmount(final String symbol)
    {
        return symbolAmountHashMap.get(symbol);
    }

    public boolean addSymbol(final String symbol)
    {
        return symbolAmountHashMap.putIfAbsent(symbol, new SymbolAmount(symbol)) == null;
    }

    public boolean removeSymbol(final String symbol)
    {
        return symbolAmountHashMap.remove(symbol) != null;
    }
}
