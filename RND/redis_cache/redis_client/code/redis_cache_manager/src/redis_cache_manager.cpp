#include "redis_cache_manager.hpp"

#include <iostream>



//using namespace sw::redis;

redis_cache_manager::redis_cache_manager(const std::string& uri): redis(uri)

{
    this->bid_increase_script_sha = this->redis.script_load(
        INCREASE_QTY_SCRIPT(BID_PRICE_LEVEL_ZSET_KEY, BID_QTY_PER_PRICE_HASH_KEY)
    );

    this->bid_decrease_script_sha = this->redis.script_load(
        DECREASE_QTY_SCRIPT(BID_PRICE_LEVEL_ZSET_KEY, BID_QTY_PER_PRICE_HASH_KEY)
    );
}

long redis_cache_manager::bid_increase(long price, long qty)
{
    auto reply = redis.evalsha<long long>(
        this->bid_increase_script_sha,
        {},
        {std::to_string(price), std::to_string(qty)}
    );

    return reply;
}

long redis_cache_manager::bid_decrease(long price, long qty)
{
    auto reply = redis.evalsha<long long>(
        this->bid_decrease_script_sha,
        {},
        {std::to_string(price), std::to_string(qty)}
    );

    return reply;
}