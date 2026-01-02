package digital.exchange.ingress.rest.server.app.resources.mepair.remove;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import digital.exchange.ingress.rest.server.aeron.infra.MeClusterSender;
import digital.exchange.ingress.rest.server.app.response.Response;
import digital.exchange.ingress.rest.server.app.resources.RequestsHandler;
import digital.exchange.ingress.rest.server.app.SeqNumberGenerator;

@Path("/remove_pair")
@ApplicationScoped
public class RemovePairResource
{

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> removePair(RemovePairRequest removePairRequest)
    {
        final long seqNum = SeqNumberGenerator.getSeqNumber();
        System.out.println(seqNum);

        Runnable runnable = () ->
        {
            boolean res = MeClusterSender.sendRemovePairMatchingEngine(removePairRequest.base(), removePairRequest.quote(), seqNum);

            if(!res)
            {
                RequestsHandler.completeRequest(seqNum, new Response("Internal Error"));
            }
        };


        var uni = RequestsHandler.addRequest(runnable, seqNum);

        return uni;
    }
}
