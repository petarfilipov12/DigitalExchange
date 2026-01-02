package digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick;

import digital.exchange.egress.hub.cluster.archive.adapter.CandleDbMgr.CandleDbMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandlestickUpdater
{
    record CandleBounds(long startTimestamp, long endTimestamp)
    {
        public static CandleBounds getCandleBoundsFromTimestamp(final long timestamp, final long intervalMs)
        {
            final long startTimestamp = (timestamp / intervalMs) * intervalMs;
            return new CandleBounds(startTimestamp, startTimestamp + intervalMs);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickUpdater.class);

    private final Candle candle = new Candle();

    private final String base;
    private final String quote;
    private final long intervalMs;

    private long lastPrice = Long.MIN_VALUE;

    public CandlestickUpdater(final String base, final String quote, long intervalMs) {
        this.base = base;
        this.quote = quote;
        this.intervalMs = intervalMs;
    }

    public void onOrderFill(final long timestamp, final long price, final long qty)
    {
        if(candle.isEmpty())
        {
            final CandleBounds candleBounds = CandleBounds.getCandleBoundsFromTimestamp(timestamp, intervalMs);
            candle.init(candleBounds.startTimestamp(), candleBounds.endTimestamp(), price, qty);
        }
        else
        {
            if( (timestamp >= candle.getStartTimestamp()) && (timestamp < candle.getEndTimestamp()) )
            {
                candle.update(price, qty);
            }
            else if(timestamp >= candle.getEndTimestamp())
            {

                emitCandle();

                final CandleBounds candleBounds = CandleBounds.getCandleBoundsFromTimestamp(timestamp, intervalMs);
                candle.init(candleBounds.startTimestamp(), candleBounds.endTimestamp(), price, qty);
            }
            else
            {
                //TODO order fill timestamp is earlier then the current candle startTimestamp
                //should not get here
            }
        }

        lastPrice = price;
    }

    public void onClockUpdate(final long timestamp)
    {
        if(lastPrice == Long.MIN_VALUE)
        {
            return;
        }

        if(timestamp < candle.getEndTimestamp())
        {
            return;
        }

        emitCandle();

        final CandleBounds candleBounds = CandleBounds.getCandleBoundsFromTimestamp(timestamp, intervalMs);
        candle.initEmpty(candleBounds.startTimestamp(), candleBounds.endTimestamp(), lastPrice);
    }

    private void emitCandle()
    {
        LOGGER.info("CANDLE EMIT: {}", candle);
        CandleDbMgr.emitCandle(base, quote, intervalMs, candle);
    }

    public String getBase()
    {
        return base;
    }

    public String getQuote()
    {
        return quote;
    }

    public long getIntervalMs()
    {
        return intervalMs;
    }
}
