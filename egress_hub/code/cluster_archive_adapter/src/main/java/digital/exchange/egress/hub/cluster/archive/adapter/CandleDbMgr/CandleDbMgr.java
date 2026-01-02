package digital.exchange.egress.hub.cluster.archive.adapter.CandleDbMgr;

import digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick.Candle;
import org.agrona.collections.Long2ObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandleDbMgr
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CandleDbMgr.class);
    private static final Long2ObjectHashMap<String> intervalMap;

    static {
        intervalMap = new Long2ObjectHashMap<>();
        intervalMap.put(60000, "1m");
        intervalMap.put(300000, "5m");
        intervalMap.put(900000, "15m");
        intervalMap.put(1800000, "30m");
        intervalMap.put(3600000, "1h");
        intervalMap.put(14400000, "4h");
        intervalMap.put(86400000, "1d");
    }

    public static void init(final String base, final String quote, final long interval)
    {
        final String tableName = getTableName(base, quote, interval);
        LOGGER.info("CreateTable: {}", tableName);
        PostgreSqlMgr.createTableIfNotExists(tableName);
    }

    public static void emitCandle(final String base, final String quote, final long interval, final Candle candle)
    {
        final String tableName = getTableName(base, quote, interval);
        LOGGER.info("Insert: {}, candle: {}", tableName, candle.toString());
        PostgreSqlMgr.insertCandle(tableName, candle);
    }

    private static String getTableName(final String base, final String quote, final long interval)
    {
        return "%s%s_%s".formatted(base, quote, intervalMap.get(interval));
    }
}
