package digital.exchange.rest.server.marketdata.app.endpoints.candles;

import digital.exchange.rest.server.marketdata.app.postgres.PostgresMgr;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/candles")
@ApplicationScoped
public class CandlesResource
{
    @Inject
    PostgresMgr postgresMgr;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Candle> getCandles(CandlesRequest candlesRequest)
    {
        final String symbol = candlesRequest.base() + candlesRequest.quote();
        switch (candlesRequest.interval())
        {
            case "1m":
                break;
            case "5m":
                break;
            case "15m":
                break;
            case "30m":
                break;
            case "1h":
                break;
            case "4h":
                break;
            case "1d":
                break;
            default:
                throw new WebApplicationException("Invalid interval", 400);
        }

        return postgresMgr.getCandles(symbol + "_" + candlesRequest.interval(), candlesRequest.limit());
    }
}

