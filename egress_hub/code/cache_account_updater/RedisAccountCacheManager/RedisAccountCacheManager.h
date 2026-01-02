#pragma once

#include <memory>
#include <string>
#include <vector>
#include <tuple>

#include <sw/redis++/redis++.h>
#include <spdlog/spdlog.h>

class RedisAccountCacheManager
{
    private:
        std::shared_ptr<spdlog::logger> logger;
        
        sw::redis::Redis redis;

        std::string accountUpdateScriptSha;

        static const char* accountUpdateScript;
        
    public:
        RedisAccountCacheManager(const std::string& uri);

        void addAccount(const int64_t accountId);
        void removeAccount(const int64_t accountId);

        long accountUpdate(
            const int64_t accountId, 
            const std::string& symbol, 
            const int64_t freeAmountChange, 
            const int64_t lockedAmountChange
        );

        long accountUpdate(std::vector<std::tuple<int64_t, std::string, int64_t, int64_t> > accountUpdates);
};