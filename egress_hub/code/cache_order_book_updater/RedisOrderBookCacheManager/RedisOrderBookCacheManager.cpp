#include "RedisOrderBookCacheManager.h"

#include <iostream>
#include <thread>
#include <chrono>

#include <spdlog/sinks/stdout_color_sinks.h>

#include "AppConfig.h"

const char* RedisOrderBookCacheManager::increaseQtyScript = R"(
    local base = ARGV[1]
    local quote = ARGV[2]

    local price = ARGV[3]
    local qty = tonumber(ARGV[4])

    local bid_ask = ARGV[5]

    local seqNum = ARGV[6]

    local orderBooksPrefix = "OrderBooks:"

    local seqNumKey = orderBooksPrefix .. base .. quote .. ":" .. "SeqNum"
    redis.call('SET', seqNumKey, seqNum)

    local zset_key = orderBooksPrefix .. base .. quote .. ":" .. bid_ask .. "PriceLevelZset"
    local hash_key = orderBooksPrefix .. base .. quote .. ":" .. bid_ask .. "QtyPerPriceHash"

    local priceLevelQty = redis.call('HINCRBY', hash_key, price, qty)

    if priceLevelQty <= qty then
        redis.call('ZADD', zset_key, price, price)
    end


    return priceLevelQty
)";

const char* RedisOrderBookCacheManager::decreaseQtyScript = R"(
    local base = ARGV[1]
    local quote = ARGV[2]

    local price = ARGV[3]
    local qty = -1 * tonumber(ARGV[4])

    local bid_ask = ARGV[5]

    local seqNum = ARGV[6]

    local orderBooksPrefix = "OrderBooks:"

    local seqNumKey = orderBooksPrefix .. base .. quote .. ":" .. "SeqNum"
    redis.call('SET', seqNumKey, seqNum)

    local zset_key = orderBooksPrefix .. base .. quote .. ":" .. bid_ask .. "PriceLevelZset"
    local hash_key = orderBooksPrefix .. base .. quote .. ":" .. bid_ask .. "QtyPerPriceHash"

    local priceLevelQty = redis.call('HINCRBY', hash_key, price, qty)

    if priceLevelQty <= 0 then
        redis.call('HDEL', hash_key, price)
        redis.call('ZREM', zset_key, price)
    end

    return priceLevelQty
)";

RedisOrderBookCacheManager::RedisOrderBookCacheManager(const std::string& uri): 
redis(uri)
{
    logger = spdlog::stdout_color_mt("RedisOrderBookCacheManager");
    logger->set_pattern(AppConfig::loggerPatern);

    this->increase_script_sha = this->redis.script_load(increaseQtyScript);
    this->decrease_script_sha = this->redis.script_load(decreaseQtyScript);
}

long RedisOrderBookCacheManager::bidIncrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum)
{
    return redis.evalsha<long long>(
        this->increase_script_sha,
        {},
        {base, quote, std::to_string(price), std::to_string(qty), "Bid", std::to_string(seqNum)}
    );
}

long RedisOrderBookCacheManager::bidDecrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum)
{
    return redis.evalsha<long long>(
        this->decrease_script_sha,
        {},
        {base, quote, std::to_string(price), std::to_string(qty), "Bid", std::to_string(seqNum)}
    );
}

long RedisOrderBookCacheManager::askIncrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum)
{
    return redis.evalsha<long long>(
        this->increase_script_sha,
        {},
        {base, quote, std::to_string(price), std::to_string(qty), "Ask", std::to_string(seqNum)}
    );
}

long RedisOrderBookCacheManager::askDecrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum)
{
    return redis.evalsha<long long>(
        this->decrease_script_sha,
        {},
        {base, quote, std::to_string(price), std::to_string(qty), "Ask", std::to_string(seqNum)}
    );
}