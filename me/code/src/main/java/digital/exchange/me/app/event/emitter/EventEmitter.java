package digital.exchange.me.app.event.emitter;

import digital.exchange.me.app.events.Event;
import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class EventEmitter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventEmitter.class);

    private final Vector<EventHandler> eventHandlers = new Vector<>();

    public void emitEvent(final Event event)
    {
        setEventSeqNum(event);
        setTimestamp(event);

        LOGGER.info("Emit Event: {}", event);

        for(EventHandler eventHandler: eventHandlers)
        {
            eventHandler.onEvent(event);
        }
    }

    public boolean addEventHandler(final EventHandler eventHandler)
    {
        if(eventHandlers.contains(eventHandler)) return false;

        eventHandlers.add(eventHandler);
        return true;
    }

    public boolean removeEventHandler(final EventHandler eventHandler)
    {
        return eventHandlers.remove(eventHandler);
    }

    private void setEventSeqNum(final Event event)
    {
        event.setSeqNum(RuntimeContext.getAndIncSeqNum());
    }

    private void setTimestamp(final Event event)
    {
        event.setTimestamp(RuntimeContext.getClusterTime());
    }
}
