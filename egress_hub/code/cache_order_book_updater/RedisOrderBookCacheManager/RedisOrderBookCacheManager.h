#pragma once

#include <memory>
#include <string>

#include <sw/redis++/redis++.h>
#include <spdlog/spdlog.h>

class RedisOrderBookCacheManager
{
    private:
        std::shared_ptr<spdlog::logger> logger;
        
        sw::redis::Redis redis;

        std::string increase_script_sha;
        std::string decrease_script_sha;

        static const char* increaseQtyScript;
        static const char* decreaseQtyScript;

    public:
        RedisOrderBookCacheManager(const std::string& uri);

        long bidIncrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum);

        long bidDecrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum);

        long askIncrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum);

        long askDecrease(const std::string& base, const std::string& quote, const long price, const long qty, const long seqNum);
};