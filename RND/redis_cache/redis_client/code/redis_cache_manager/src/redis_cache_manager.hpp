#pragma once

#include <sw/redis++/redis++.h>
#include <string>

#define BID_PRICE_LEVEL_ZSET_KEY "BidPriceLevelZset"
#define BID_QTY_PER_PRICE_HASH_KEY "BidQtyPerPriceHash"

#define ASK_PRICE_LEVEL_ZSET_KEY "AskPriceLevelZset"
#define ASK_QTY_PER_PRICE_HASH_KEY "AskQtyPerPriceHash"

#define INCREASE_QTY_SCRIPT(zset_key, hash_key)     \
    "local price = ARGV[1]                                          \
    local qty = ARGV[2]                                             \
    redis.call('ZADD', '" zset_key "', price, price)                \
    return redis.call('HINCRBY', '" hash_key "', price, qty)"

#define DECREASE_QTY_SCRIPT(zset_key, hash_key)     \
    "local price = ARGV[1]                                                              \
    local qty = -1 * tonumber(ARGV[2])                                                  \
    local priceLevelQty = redis.call('HINCRBY', '" hash_key "', price, qty)             \
    if priceLevelQty <= 0 then                                                          \
        redis.call('HDEL', '" hash_key "', price)                                       \
        redis.call('ZREM', '" zset_key "', price)                                       \
    end                                                                                 \
    return priceLevelQty"

class redis_cache_manager
{
    private:
        sw::redis::Redis redis;

        std::string bid_increase_script_sha;
        std::string bid_decrease_script_sha;

    public:
        redis_cache_manager(const std::string& uri);

        long bid_increase(long price, long qty);

        long bid_decrease(long price, long qty);
};