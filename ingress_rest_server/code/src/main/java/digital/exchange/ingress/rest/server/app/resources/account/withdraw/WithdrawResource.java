package digital.exchange.ingress.rest.server.app.resources.account.withdraw;

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

@Path("/withdraw")
@ApplicationScoped
public class WithdrawResource
{

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createAccount(WithdrawRequest withdrawRequest)
    {
        final long seqNum = SeqNumberGenerator.getSeqNumber();
        System.out.println(seqNum);

        Runnable runnable = () ->
        {
            boolean res = MeClusterSender.sendWithdraw(
                    withdrawRequest.accountId(),
                    withdrawRequest.symbol(),
                    withdrawRequest.qty(),
                    seqNum
            );

            if(!res)
            {
                RequestsHandler.completeRequest(seqNum, new Response("Internal Error"));
            }
        };


        var uni = RequestsHandler.addRequest(runnable, seqNum);

        return uni;
    }
}
