package digital.exchange.rest.server.marketdata.app.postgres;

import digital.exchange.rest.server.marketdata.app.endpoints.candles.Candle;
import digital.exchange.rest.server.marketdata.app.endpoints.trades.Trade;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PostgresMgr
{
    @Inject
    DataSource dataSource;

    public List<Trade> getTrades(final String tableName, final int limit){
        String sql = String.format("""
            SELECT seqNum, timestamp, takerAccountId, takerOrderId, takerOrderSide,
                   makerAccountId, makerOrderId, makerOrderSide, price, qty
            FROM tradeSchema.%s
            ORDER BY timestamp DESC
            LIMIT ?
        """, tableName);

        final List<Trade> trades = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Trade trade = new Trade(
                        rs.getLong("seqNum"),
                        rs.getObject("timestamp", OffsetDateTime.class),
                        rs.getLong("takerAccountId"),
                        rs.getLong("takerOrderId"),
                        rs.getString("takerOrderSide"),
                        rs.getLong("makerAccountId"),
                        rs.getLong("makerOrderId"),
                        rs.getString("makerOrderSide"),
                        rs.getLong("price"),
                        rs.getLong("qty")
                    );
                    trades.add(trade);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return trades;
    }

    public List<Candle> getCandles(String tableName, int limit) {
        String sql = String.format("""
            SELECT startTimestamp, endTimestamp, open, high, low, close, volume
            FROM candleSchema.%s
            ORDER BY startTimestamp DESC
            LIMIT ?
        """, tableName);

        final List<Candle> candles = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Candle candle = new Candle(
                        rs.getLong("startTimestamp"),
                        rs.getLong("endTimestamp"),
                        rs.getLong("open"),
                        rs.getLong("high"),
                        rs.getLong("low"),
                        rs.getLong("close"),
                        rs.getLong("volume")
                    );
                    candles.add(candle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return candles;
    }
}
