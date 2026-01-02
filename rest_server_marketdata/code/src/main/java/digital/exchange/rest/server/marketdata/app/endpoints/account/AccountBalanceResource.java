package digital.exchange.rest.server.marketdata.app.endpoints.account;

import digital.exchange.rest.server.marketdata.app.redis.RedisMgr;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/account_balance")
@ApplicationScoped
public class AccountBalanceResource
{
    @Inject
    RedisMgr redisMgr;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrderBook(AccountBalanceRequest accountBalanceRequest)
    {
        return redisMgr.getAccount(accountBalanceRequest.accountId());
    }
}
