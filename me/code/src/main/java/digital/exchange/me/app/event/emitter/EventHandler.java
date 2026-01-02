package digital.exchange.me.app.event.emitter;

import digital.exchange.me.app.events.Event;

@FunctionalInterface
public interface EventHandler
{
    void onEvent(Event event);
}
