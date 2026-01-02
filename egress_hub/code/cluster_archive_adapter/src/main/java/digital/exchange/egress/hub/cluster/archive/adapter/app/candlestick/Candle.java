package digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick;

public class Candle
{
    private long startTimestamp;
    private long endTimestamp;
    private long openPrice;
    private long highPrice;
    private long lowPrice;
    private long closePrice; //lastPrice
    private long volume;

    private boolean empty = true;

    public void initEmpty(final long startTimestamp, final long endTimestamp, final long lastCandleClosePrice)
    {
        init(startTimestamp, endTimestamp, lastCandleClosePrice, 0);
        empty = true;
    }

    public void init(final long startTimestamp, final long endTimestamp, final long price, final long qty)
    {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;

        openPrice = highPrice = lowPrice = closePrice = price;
        volume = qty;

        empty = false;
    }

    public void update(final long price, final long qty)
    {
        closePrice = price;
        volume += qty;

        if(price < lowPrice)
        {
            lowPrice = price;
        }
        else if(price > highPrice)
        {
            highPrice = price;
        }

        empty = false;
    }

    public long getStartTimestamp()
    {
        return startTimestamp;
    }

    public long getEndTimestamp()
    {
        return endTimestamp;
    }

    public long getOpenPrice()
    {
        return openPrice;
    }

    public long getHighPrice()
    {
        return highPrice;
    }

    public long getLowPrice()
    {
        return lowPrice;
    }

    public long getClosePrice()
    {
        return closePrice;
    }

    public long getVolume()
    {
        return volume;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public String toString()
    {
        String s = "Candle - startTimestamp: " + startTimestamp;
        s += ", endTimestamp: " + endTimestamp;
        s += ", openPrice: " + openPrice;
        s += ", highPrice: " + highPrice;
        s += ", lowPrice: " + lowPrice;
        s += ", closePrice: " + closePrice;
        s += ", volume: " + volume;
        s += ", empty: " + empty;

        return s;
    }

}
