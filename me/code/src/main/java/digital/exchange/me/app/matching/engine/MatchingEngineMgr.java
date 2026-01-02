package digital.exchange.me.app.matching.engine;

import digital.exchange.me.app.event.emitter.EventEmitter;
import org.agrona.collections.Object2ObjectHashMap;

public class MatchingEngineMgr
{
    private final Object2ObjectHashMap<String, MatchingEngine> matchingEngineHashMap = new Object2ObjectHashMap<>();
    private final EventEmitter eventEmitter;

    public MatchingEngineMgr(EventEmitter eventEmitter)
    {
        this.eventEmitter = eventEmitter;
    }

    public boolean matchingEngineExists(final String base, final String quote)
    {
        return matchingEngineHashMap.containsKey(getPair(base, quote));
    }

    public MatchingEngine getMatchingEngine(final String base, final String quote)
    {
        return matchingEngineHashMap.get(getPair(base, quote));
    }

    public boolean addMatchingEngine(final String base, final String quote)
    {
        return matchingEngineHashMap.putIfAbsent(getPair(base, quote), new MatchingEngine(base, quote, eventEmitter)) == null;
    }

    public boolean removeMatchingEngine(final String base, final String quote)
    {
        return matchingEngineHashMap.remove(getPair(base, quote)) != null;
    }

    private String getPair(final String base, final String quote)
    {
        return base + "_" + quote;
    }

}
