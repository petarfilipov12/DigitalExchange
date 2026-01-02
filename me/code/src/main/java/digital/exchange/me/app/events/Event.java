package digital.exchange.me.app.events;

public class Event
{
    private final EventId eventId;
    private long seqNum;
    private long timestamp;

    Event()
    {
        eventId = EventId.EVENT_ID_INVALID;
    }

    Event(EventId eventId)
    {
        this.eventId = eventId;
    }

    public EventId getEventId() {
        return eventId;
    }

    public long getSeqNum()
    {
        return seqNum;
    }

    public void setSeqNum(final long seqNum)
    {
        this.seqNum = seqNum;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return  eventId + ": seqNum: " + seqNum + ", timestamp: " + timestamp;
    }
}
