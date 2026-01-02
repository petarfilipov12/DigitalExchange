package digital.exchange.me.infra.cluster.runtime.context;

import io.aeron.cluster.service.ClientSession;

import java.util.ArrayList;
import java.util.List;

public class SubscribedSessions
{
    private final List<ClientSession> sessions = new ArrayList<>();

    public void addSession(final ClientSession session)
    {
        sessions.add(session);
    }

    public void removeSession(final ClientSession session)
    {
        sessions.remove(session);
    }

    public List<ClientSession> getSessions() {
        return sessions;
    }
}
