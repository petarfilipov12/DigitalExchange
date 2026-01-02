package digital.exchange.me.infra.cluster;

import digital.exchange.me.app.ClockUpdateEventEmitter;
import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;

public class ClockUpdater
{
    private static final long clockUpdateIntervalMs = 60*1000;

    private long currentTimerEventCorrelationId = 0;

    private final ClockUpdateEventEmitter clockUpdateEventEmitter;

    public ClockUpdater(final ClockUpdateEventEmitter clockUpdateEventEmitter) {
        this.clockUpdateEventEmitter = clockUpdateEventEmitter;
    }

    public void init(final long timestamp)
    {
        scheduleNextTimerEventFromTimestamp(timestamp);
    }

    public void onTimerEvent(final long correlationId, final long timestamp)
    {
        clockUpdateEventEmitter.emitClockUpdateEvent(timestamp);
        scheduleNextTimerEventFromTimestamp(timestamp);
    }

    private void scheduleNextTimerEventFromTimestamp(final long timestamp)
    {
        final long deadline = ((timestamp / clockUpdateIntervalMs) * clockUpdateIntervalMs) + clockUpdateIntervalMs;

        boolean ret = RuntimeContext.getCluster().scheduleTimer(currentTimerEventCorrelationId , deadline);
        while(!ret)
        {
            ret = RuntimeContext.getCluster().scheduleTimer(currentTimerEventCorrelationId , deadline);
        }
        currentTimerEventCorrelationId++;
    }
}
