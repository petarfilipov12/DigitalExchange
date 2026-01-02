package digital.exchange.me.app.account;

import org.agrona.collections.Long2ObjectHashMap;

public class AccountMgr
{
    private final Long2ObjectHashMap<Account> accountDataHashMap = new Long2ObjectHashMap<>();

    public boolean accountExists(final long accountId)
    {
        return accountDataHashMap.containsKey(accountId);
    }

    public Account getAccount(final long accountId)
    {
        return accountDataHashMap.get(accountId);
    }

    public boolean addAccount(final long accountId)
    {
        return accountDataHashMap.putIfAbsent(accountId, new Account(accountId)) == null;
    }

    public boolean removeAccount(final long accountId)
    {
        return accountDataHashMap.remove(accountId) != null;
    }

}
