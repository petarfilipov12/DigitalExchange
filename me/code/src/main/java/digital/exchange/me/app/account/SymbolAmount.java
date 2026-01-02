package digital.exchange.me.app.account;

public class SymbolAmount
{
    private final String symbol;
    private long freeAmount;
    private long lockedAmount;

    public SymbolAmount(String symbol)
    {
        this.symbol = symbol;
        this.freeAmount = 0;
        this.lockedAmount = 0;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public long getLockedAmount() {
        return lockedAmount;
    }

    public long getFreeAmount() {
        return freeAmount;
    }

    public long getTotal()
    {
        return freeAmount + lockedAmount;
    }

    public boolean lockAmount(final long amount)
    {
        if(freeAmount < amount)
        {
            return false;
        }

        freeAmount -= amount;
        lockedAmount += amount;

        return true;
    }

    public boolean freeLockedAmount(final long amount)
    {
        if(lockedAmount < amount)
        {
            return false;
        }

        freeAmount += amount;
        lockedAmount -= amount;

        return true;
    }

    public boolean decreaseLockedAmount(final long amount)
    {
        if(lockedAmount < amount)
        {
            return false;
        }

        lockedAmount -= amount;

        return true;
    }

    public void deposit(final long amount)
    {
        freeAmount += amount;
    }

    public boolean withdraw(final long amount)
    {
        if(freeAmount < amount)
        {
            return false;
        }

        freeAmount -= amount;

        return true;
    }
}
