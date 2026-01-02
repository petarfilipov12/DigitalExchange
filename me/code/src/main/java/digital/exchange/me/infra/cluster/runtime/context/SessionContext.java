package digital.exchange.me.infra.cluster.runtime.context;

import io.aeron.Publication;
import io.aeron.cluster.service.ClientSession;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionContext
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionContext.class);

    private static final long RETRY_COUNT = 3;
    private static final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy();
    private ClientSession currentSession;

    public void setCurrentSession(final ClientSession currentSession) {
        this.currentSession = currentSession;
    }

    public ClientSession getCurrentSession() {
        return currentSession;
    }

    public void replyCurrentSession(final DirectBuffer buffer, final int offset, final int length)
    {
        offerToSession(currentSession, buffer, offset, length);
    }

    public static void offerToSession(
            final ClientSession session,
            final DirectBuffer buffer,
            final int offset,
            final int length)
    {
        int retries = 0;

        do
        {
            final long result = session.offer(buffer, offset, length);
            if (result > 0L)
            {
                return;
            }
            else if (result == Publication.ADMIN_ACTION || result == Publication.BACK_PRESSURED)
            {
                LOGGER.warn("Backpressure or Admin action on session offer");
            }
            else if (result == Publication.NOT_CONNECTED || result == Publication.MAX_POSITION_EXCEEDED)
            {
                LOGGER.error("Unexpected state on session offer: {}", result);
                return;
            }

            idleStrategy.idle();
            retries++;
        }
        while (retries < RETRY_COUNT);

        LOGGER.error("Failed to offer to session within {} retries. Closing client session.", RETRY_COUNT);
        session.close();
    }
}
