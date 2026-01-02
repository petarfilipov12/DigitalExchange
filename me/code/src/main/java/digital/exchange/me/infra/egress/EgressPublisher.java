package digital.exchange.me.infra.egress;

import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import digital.exchange.me.infra.cluster.runtime.context.SessionContext;
import org.agrona.DirectBuffer;

public class EgressPublisher
{
    public void publish(final DirectBuffer buffer, final int length)
    {
        RuntimeContext.getSubscribedSessions().getSessions().forEach(
                session -> SessionContext.offerToSession(session, buffer, 0, length));
    }
}
