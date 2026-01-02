#include "RedisAccountCacheManager.h"

#include <iostream>
#include <thread>
#include <chrono>

#include <spdlog/sinks/stdout_color_sinks.h>

#include "AppConfig.h"

const char* RedisAccountCacheManager::accountUpdateScript = R"(
    local accountId = ARGV[1]
    local symbol = ARGV[2]

    local hashKey = "Accounts:" .. accountId .. ":" .. symbol
    local setKey = "Accounts:" .. accountId .. ":Holdings"

    local freeAmountChange = tonumber(ARGV[3])
    local lockedAmountChange = tonumber(ARGV[4])

    local freeFieldName = "free"
    local lockedFiledName = "locked"

    local freeAmountTotal = redis.call('HINCRBY', hashKey, freeFieldName, freeAmountChange)
    local lockedAmountTotal = redis.call('HINCRBY', hashKey, lockedFiledName, lockedAmountChange)

    if(freeAmountTotal == 0 and lockedAmountTotal == 0) then
        redis.call('HDEL', hashKey, freeFieldName, lockedFiledName)
        redis.call('SREM', setKey, symbol)
    else
        redis.call('SADD', setKey, symbol)
    end

    return 1
)";

RedisAccountCacheManager::RedisAccountCacheManager(const std::string& uri): 
redis(uri)
{
    logger = spdlog::stdout_color_mt("RedisAccountCacheManager");
    logger->set_pattern(AppConfig::loggerPatern);

    accountUpdateScriptSha = redis.script_load(accountUpdateScript);
}

void RedisAccountCacheManager::addAccount(const int64_t accountId)
{
    redis.sadd("Accounts:AllAccountsSet", std::to_string(accountId));
}

void RedisAccountCacheManager::removeAccount(const int64_t accountId)
{
    redis.srem("Accounts:AllAccountsSet", std::to_string(accountId));
}

long RedisAccountCacheManager::accountUpdate(
    const int64_t accountId, 
    const std::string& symbol, 
    const int64_t freeAmountChange, 
    const int64_t lockedAmountChange
)
{
    return redis.evalsha<long long>(
        accountUpdateScriptSha,
        {},
        {std::to_string(accountId), symbol, std::to_string(freeAmountChange), std::to_string(lockedAmountChange)}
    );
}

long RedisAccountCacheManager::accountUpdate(
    std::vector<std::tuple<int64_t, std::string, int64_t, int64_t> > accountUpdates
)
{
    if(accountUpdates.size() <= 0)
    {
        return 0;
    }

    auto pipe = redis.pipeline(false);

    for(const auto& [accountId, symbol, freeAmountChange, lockedAmountChange] : accountUpdates)
    {
        pipe.evalsha(
            accountUpdateScriptSha,
            {},
            {std::to_string(accountId), symbol, std::to_string(freeAmountChange), std::to_string(lockedAmountChange)}
        );
    }

    auto repl = pipe.exec();

    return 1;
}