package digital.exchange.ingress.rest.server.app.resources;

import io.netty.util.Timeout;
import io.smallrye.mutiny.subscription.UniEmitter;
import digital.exchange.ingress.rest.server.app.response.Response;

public record PendingRequest(UniEmitter<Response> emitter, Timeout timeout) {}
