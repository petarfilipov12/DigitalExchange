package digital.exchange.rest.server.marketdata.app.endpoints.orderbook;

import digital.exchange.rest.server.marketdata.app.redis.RedisMgr;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/order_book")
@ApplicationScoped
public class OrderBookResource
{
    @Inject
    RedisMgr redisMgr;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrderBook(OrderBookRequest orderBookRequest)
    {
        return redisMgr.getOrderBook(
                orderBookRequest.base(),
                orderBookRequest.quote(),
                orderBookRequest.depth()
        );
    }
}

