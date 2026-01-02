package digital.exchange.egress.hub.cluster.archive.adapter.CandleDbMgr;

import digital.exchange.egress.hub.cluster.archive.adapter.app.candlestick.Candle;
import digital.exchange.egress.hub.cluster.archive.adapter.utils.AppConfig;

import java.sql.*;

public class PostgreSqlMgr
{
    static
    {
        final String sql = "CREATE SCHEMA IF NOT EXISTS candleSchema";

        try(
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        )
        {
            statement.execute(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableIfNotExists(final String tableName)
    {
        final String sql = """
                CREATE TABLE IF NOT EXISTS candleSchema.%s (
                    startTimestamp BIGINT PRIMARY KEY,
                    endTimestamp BIGINT NOT NULL,
                    open BIGINT NOT NULL,
                    high BIGINT NOT NULL,
                    low BIGINT NOT NULL,
                    close BIGINT NOT NULL,
                    volume BIGINT NOT NULL
                )
                """.formatted(tableName);

        try(
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        )
        {
            statement.execute(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertCandle(final String tableName, final Candle candle)
    {
        final String sql = """
                INSERT INTO candleSchema.%s (startTimestamp, endTimestamp, open, high, low, close, volume)
                VALUES(?,?,?,?,?,?,?)
                ON CONFLICT (startTimestamp) DO UPDATE SET
                    endTimestamp = EXCLUDED.endTimestamp,
                    open = EXCLUDED.open,
                    high = EXCLUDED.high,
                    low = EXCLUDED.low,
                    close = EXCLUDED.close,
                    volume = EXCLUDED.volume
                """.formatted(tableName);

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        )
        {
            preparedStatement.setLong(1, candle.getStartTimestamp());
            preparedStatement.setLong(2, candle.getEndTimestamp());
            preparedStatement.setLong(3, candle.getOpenPrice());
            preparedStatement.setLong(4, candle.getHighPrice());
            preparedStatement.setLong(5, candle.getLowPrice());
            preparedStatement.setLong(6, candle.getClosePrice());
            preparedStatement.setLong(7, candle.getVolume());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                AppConfig.dbUrl,
                AppConfig.dbUser,
                AppConfig.dbPassword
        );
    }
}
