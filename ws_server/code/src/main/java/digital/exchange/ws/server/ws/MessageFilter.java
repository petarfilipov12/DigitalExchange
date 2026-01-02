package digital.exchange.ws.server.ws;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import digital.exchange.ws.server.ws.event.messages.EventMessageType;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageFilter
{
    private final Map<EventMessageType, Set<String>> filters = new EnumMap<>(EventMessageType.class);

    public MessageFilter()
    {
        for(EventMessageType messageType : EventMessageType.values())
        {
            filters.put(messageType, new HashSet<>());
        }
    }

    public Set<String> getFilter(final EventMessageType messageType)
    {
        return filters.get(messageType);
    }

    public boolean wants(final EventMessageType messageType, final String symbol)
    {
        return filters.get(messageType).contains(symbol);
    }

    @JsonAnySetter
    public void set(final String key, final Set<String> symbols)
    {
        final EventMessageType messageType = EventMessageType.valueOf(key);
        filters.put(messageType, symbols);
    }
}
