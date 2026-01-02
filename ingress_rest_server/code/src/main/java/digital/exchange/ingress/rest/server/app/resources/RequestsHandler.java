package digital.exchange.ingress.rest.server.app.resources;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniEmitter;
import digital.exchange.ingress.rest.server.app.response.Response;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestsHandler
{
    private static final HashedWheelTimer timer = new HashedWheelTimer();
    private static final ConcurrentHashMap<Long, PendingRequest> pendingRequests = new ConcurrentHashMap<>();
    private static final long timeoutMillis = 3000;

    public static Uni<Response> addRequest(final Runnable runnable, final long requestId)
    {
        Uni<Response> res = Uni.createFrom().emitter(uniEmitter -> {
            Timeout timeout = timer.newTimeout(timeoutTask -> {
                failRequest(requestId);
            }, timeoutMillis, TimeUnit.MILLISECONDS);

            var pendingRequest = new PendingRequest((UniEmitter<Response>) uniEmitter, timeout);
            pendingRequests.put(requestId, pendingRequest);

            runnable.run();
        });

        return res;
    }

    public static <T extends Response> Uni<T> addTypedRequest(final Runnable runnable, final long requestId)
    {
        return addRequest(runnable, requestId).onItem().transform(response -> (T) response);
    }

    public static boolean completeRequest(final long requestId, final Response response)
    {
        var pendingRequest = pendingRequests.remove(requestId);
        if(pendingRequest != null)
        {
            pendingRequest.timeout().cancel();
            pendingRequest.emitter().complete(response);

            return true;
        }

        return false;
    }

    public static void failRequest(final long requestId)
    {
        var pendingRequest = pendingRequests.remove(requestId);
        if(pendingRequest != null)
        {
            pendingRequest.emitter().fail(new TimeoutException("Request timed out for requestId: " + requestId));
        }
    }
}
