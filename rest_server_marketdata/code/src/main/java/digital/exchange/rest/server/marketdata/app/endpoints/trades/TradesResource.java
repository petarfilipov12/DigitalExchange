package digital.exchange.rest.server.marketdata.app.endpoints.trades;

import digital.exchange.rest.server.marketdata.app.postgres.PostgresMgr;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/trades")
@ApplicationScoped
public class TradesResource
{
    @Inject
    PostgresMgr postgresMgr;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Trade> getTrades(TradesRequest tradesRequest)
    {
        return postgresMgr.getTrades(tradesRequest.base() + tradesRequest.quote(), tradesRequest.limit());
    }
}

