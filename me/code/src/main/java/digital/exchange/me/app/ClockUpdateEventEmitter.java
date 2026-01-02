package digital.exchange.me.app;

import digital.exchange.me.app.event.emitter.EventEmitter;
import digital.exchange.me.app.events.EventClockUpdate;

public class ClockUpdateEventEmitter
{
    private final EventEmitter eventEmitter;

    public ClockUpdateEventEmitter(final EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    public void emitClockUpdateEvent(final long timestamp)
    {
        //timestamp is set in eventEmitter.emitEvent
        eventEmitter.emitEvent(new EventClockUpdate());
    }
}
