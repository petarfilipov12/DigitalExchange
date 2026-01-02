package digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick;

import digital.exchange.egress.hub.cluster.archive.adapter.CandleDbMgr.CandleDbMgr;

public class CandleStickUpdatersPerSymbol
{
    private static final long[] intervalsMs = {
            60000,      //1m
            300000,     //5m
            900000,     //15m
            1800000,    //30m
            3600000,    //1h
            14400000,   //4h
            86400000    //1d
    };

    private final CandlestickUpdater[] candlestickUpdaterArr= new CandlestickUpdater[intervalsMs.length];

    private final String base;
    private final String quote;

    public CandleStickUpdatersPerSymbol(final String base, final String quote) {
        this.base = base;
        this.quote = quote;

        for(int i = 0; i < intervalsMs.length; i++)
        {
            candlestickUpdaterArr[i] = new CandlestickUpdater(base, quote, intervalsMs[i]);
            CandleDbMgr.init(base, quote, intervalsMs[i]);
        }
    }

    public void orderFill(final long timestamp, final long price, final long qty)
    {
        for(CandlestickUpdater candlestickUpdater: candlestickUpdaterArr)
        {
            candlestickUpdater.onOrderFill(timestamp, price, qty);
        }
    }

    public void clockUpdate(final long timestamp)
    {
        for(CandlestickUpdater candlestickUpdater: candlestickUpdaterArr)
        {
            candlestickUpdater.onClockUpdate(timestamp);
        }
    }

    public String getBase() {
        return base;
    }

    public String getQuote() {
        return quote;
    }
}
