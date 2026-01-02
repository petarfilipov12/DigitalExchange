package digital.exchange.rest.server.marketdata.app.redis;

import digital.exchange.rest.server.marketdata.utils.AppConfig;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import redis.clients.jedis.JedisPooled;

import java.util.List;

@Startup
@ApplicationScoped
public class RedisMgr
{
    private final JedisPooled jedis = new JedisPooled(AppConfig.redisDbHost, AppConfig.redisDbPort);

    private final String getOrderBookSha1;
    private final String getAccountSha1;

    public RedisMgr()
    {
        getOrderBookSha1 = jedis.scriptLoad(RedisEvalScripts.getOrderBookScript);
        getAccountSha1 = jedis.scriptLoad(RedisEvalScripts.getAccountScript);
    }

    public String getOrderBook(final String base, final String quote, final int depth)
    {
        var data = jedis.evalsha(
                getOrderBookSha1,
                List.<String>of(),
                List.of(base, quote, Integer.toString(depth))
        );

        return data.toString();
    }

    public String getAccount(final long accountId)
    {
        var data = jedis.evalshaReadonly(
                getAccountSha1,
                List.<String>of(),
                List.of(Long.toString(accountId))
        );

        return data.toString();
    }
}
