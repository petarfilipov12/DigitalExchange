package digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick;

import java.util.LinkedHashMap;

public class CandlestickUpdaters
{
    private final LinkedHashMap<String, CandleStickUpdatersPerSymbol> candlestickUpdatersMap = new LinkedHashMap<>();

    public void addPair(final long timestamp, final String base, final String quote)
    {
        clockUpdate(timestamp);
        candlestickUpdatersMap.putIfAbsent(getPairSymbol(base, quote), new CandleStickUpdatersPerSymbol(base, quote));
    }

    public void removePair(final long timestamp, final String base, final String quote)
    {
        candlestickUpdatersMap.remove(getPairSymbol(base, quote));
        clockUpdate(timestamp);
    }

    public void clockUpdate(final long timestamp)
    {
        for(CandleStickUpdatersPerSymbol candleStickUpdater : candlestickUpdatersMap.values())
        {
            candleStickUpdater.clockUpdate(timestamp);
        }
    }

    public void orderFill(final long timestamp, final String base, final String quote, final long price, final long qty)
    {
        final CandleStickUpdatersPerSymbol candleStickUpdater = candlestickUpdatersMap.get(getPairSymbol(base, quote));

        if(candleStickUpdater != null)
        {
            candleStickUpdater.orderFill(timestamp, price, qty);
        }

        clockUpdate(timestamp);
    }

    private String getPairSymbol(final String base, final String quote)
    {
        return base + quote;
    }
}
