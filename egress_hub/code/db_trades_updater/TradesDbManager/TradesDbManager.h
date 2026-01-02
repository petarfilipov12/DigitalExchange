#pragma once

#include <memory>
#include <string>

#include <pqxx/pqxx>
#include <spdlog/spdlog.h>

class TradesDbManager
{
    private:
        std::shared_ptr<spdlog::logger> logger;

        pqxx::connection connection;

        void execute(const std::string& query);

        void createSchemaIfNotExist();

        void createOrderSideEnumIfNotExist();

        static std::string getTableName(const std::string& base, const std::string& quote);

    public:
        TradesDbManager();

        long addPair(const std::string& base, const std::string& quote);

        long orderFill(
            const std::string& base, 
            const std::string& quote, 
            const long seqNum,
            const long timestamp,
            const long takerAccountId,
            const long takerOrderId,
            const std::string &takerOrderSide,
            const long makerAccountId,
            const long makerOrderId,
            const std::string &makerOrderSide,
            const long price, 
            const long qty
        );
};