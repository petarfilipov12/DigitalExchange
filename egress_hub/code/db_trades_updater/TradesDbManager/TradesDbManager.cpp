#include "TradesDbManager.h"

#include <algorithm>
#include <chrono>
#include <fmt/core.h>

#include <spdlog/sinks/stdout_color_sinks.h>

#include "SbeUtilsMeOut.h"
#include "AppConfig.h"

TradesDbManager::TradesDbManager():
connection(fmt::format("host={} dbname={} user={} password={}", 
    AppConfig::dbTradesHost, AppConfig::dbTradesDbName, AppConfig::dbTradesUser, AppConfig::dbTradesPassword))
{
    if(!connection.is_open())
    {
        throw std::runtime_error("Failed to open db connection");
    }

    createSchemaIfNotExist();
    createOrderSideEnumIfNotExist();
}

void TradesDbManager::execute(const std::string& query)
{
    pqxx::work txn(connection);

    txn.exec(query);
    txn.commit();
}

void TradesDbManager::createSchemaIfNotExist()
{
    const std::string query = "CREATE SCHEMA IF NOT EXISTS tradeSchema";

    execute(query);
}

void TradesDbManager::createOrderSideEnumIfNotExist()
{
    const std::string enums = fmt::format("'{}'", fmt::join(SbeUtilsMeOut::getOrderSides(), "', '"));

    const std::string query = fmt::format(R"(
                    DO $$
                    BEGIN
                        CREATE TYPE tradeSchema.orderside AS ENUM ({});
                    EXCEPTION
                        WHEN duplicate_object THEN
                            NULL;
                    END
                    $$;
        )",
        enums
    );

    execute(query);
}

std::string TradesDbManager::getTableName(const std::string& base, const std::string& quote)
{
    std::string tableName = base + quote;

    std::transform(tableName.begin(), tableName.end(), tableName.begin(),
        [](unsigned char c)
        {
            return static_cast<char>(std::toupper(c));
        }
    );

    return tableName;
}

long TradesDbManager::addPair(const std::string &base, const std::string &quote)
{
    const std::string query = fmt::format(R"(
                    CREATE TABLE IF NOT EXISTS tradeSchema.{} (
                    seqNum BIGINT PRIMARY KEY,
                    timestamp TIMESTAMPTZ NOT NULL,
                    takerAccountId BIGINT NOT NULL,
                    takerOrderId BIGINT NOT NULL,
                    takerOrderSide tradeSchema.orderside NOT NULL,
                    makerAccountId BIGINT NOT NULL,
                    makerOrderId BIGINT NOT NULL,
                    makerOrderSide tradeSchema.orderside NOT NULL,
                    price BIGINT NOT NULL,
                    qty BIGINT NOT NULL
                );
        )",
        getTableName(base, quote)
    );

    execute(query);

    return 1;
}

long TradesDbManager::orderFill(
    const std::string &base,
    const std::string &quote,
    const long seqNum,
    const long timestamp,
    const long takerAccountId,
    const long takerOrderId,
    const std::string &takerOrderSide,
    const long makerAccountId,
    const long makerOrderId,
    const std::string &makerOrderSide,
    const long price,
    const long qty)
{
    const std::string query = fmt::format(R"(
                    INSERT INTO tradeSchema.{} (seqNum, timestamp, takerAccountId, takerOrderId, takerOrderSide, makerAccountId, makerOrderId, makerOrderSide, price, qty) 
                    VALUES ($1, to_timestamp($2), $3, $4, $5::tradeSchema.orderSide, $6, $7, $8::tradeSchema.orderSide, $9, $10);
        )",
        getTableName(base, quote)
    );

    double tp = timestamp / 1000.0;

    pqxx::work txn(connection);

    txn.exec_params(
        query,
        seqNum,
        tp,
        takerAccountId,
        takerOrderId,
        takerOrderSide,
        makerAccountId,
        makerOrderId,
        makerOrderSide,
        price,
        qty
    );

    txn.commit();

    return 1;
}