package digital.exchange.rest.server.marketdata.app.redis;

public class RedisEvalScripts
{
    public static final String getOrderBookScript = """
        local base = ARGV[1]
        local quote = ARGV[2]
        local depth = tonumber(ARGV[3]) or 10
        
        local orderBooksPrefix = "OrderBooks:"
        
        local seqNumKey = orderBooksPrefix .. base .. quote .. ":" .. "SeqNum"
        
        local zset_key_bid = orderBooksPrefix .. base .. quote .. ":BidPriceLevelZset"
        local zset_key_ask = orderBooksPrefix .. base .. quote .. ":AskPriceLevelZset"
        
        local hash_key_bid = orderBooksPrefix .. base .. quote .. ":BidQtyPerPriceHash"
        local hash_key_ask = orderBooksPrefix .. base .. quote .. ":AskQtyPerPriceHash"
        
        local bidPriceLevels = redis.call('ZREVRANGE', zset_key_bid, 0, depth - 1)
        local askPriceLevels = redis.call('ZRANGE', zset_key_ask, 0, depth - 1)
        
        local function populate(priceLevels, hashKey)
            local ret = ""
            for i = 1, #priceLevels, 1 do
                local price = priceLevels[i]
                local qty = redis.call('HGET', hashKey, price)
                
                if i > 1 then
                    ret = ret .. ','
                end
                ret = ret .. string.format('[%s, %s]', price, qty)
            end
            return ret
        end
        
        local seqNum = redis.call('GET', seqNumKey)
        if not seqNum then
            redis.call("SET", seqNumKey, "0")
            seqNum = "0"
        end
        
        local ret = '{"seqNum": ' .. seqNum .. ','
        ret = ret .. '"bids": ['
        ret = ret .. populate(bidPriceLevels, hash_key_bid)
        ret = ret .. '], "asks": ['
        ret = ret .. populate(askPriceLevels, hash_key_ask)
        ret = ret .. "]}"
        
        return ret
    """;

    public static final String getAccountScript = """
        local accountId = ARGV[1]
        
        local accountKeyPrefix = "Accounts:" .. accountId
        
        local holdingsSetKey = accountKeyPrefix .. ":Holdings"
        
        local holdings = redis.call('SMEMBERS', holdingsSetKey)
        
        local ret = "["
        for i, symbol in ipairs(holdings) do
            local hashKey = accountKeyPrefix .. ":" .. symbol
            
            local amounts = redis.call('HMGET', hashKey, "free", "locked")
            
            if i > 1 then
                ret = ret .. ','
            end
            ret = ret .. string.format('{"%s": {"free": %s, "locked:" %s}}', symbol, amounts[1], amounts[2])
        end
        
        ret = ret .. "]"
        
        return ret
    """;
}
