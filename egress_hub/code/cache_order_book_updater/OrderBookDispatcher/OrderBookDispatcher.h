#pragma once

#include <string>

#include "digital_exchange_sbe_me_output/MessageHeader.h"
#include "digital_exchange_sbe_me_output/MakerOrderCanceledEvent.h"
#include "digital_exchange_sbe_me_output/MakerOrderAddedEvent.h"
#include "digital_exchange_sbe_me_output/OrderFillEvent.h"

#include "RedisOrderBookCacheManager.h"

class OrderBookDispatcher
{
private:
    digital::exchange::sbe::me::output::MessageHeader messageHeaderDecoder;
    digital::exchange::sbe::me::output::MakerOrderCanceledEvent orderCanceledEventDecoder;
    digital::exchange::sbe::me::output::MakerOrderAddedEvent orderAddedEventDecoder;
    digital::exchange::sbe::me::output::OrderFillEvent orderFillEventDecoder;

    RedisOrderBookCacheManager redisManager;

    bool dispatchOrderCandeledEvent(char *buffer, int64_t offset, int64_t length);

    bool dispatchOrderAddedEvent(char *buffer, int64_t offset, int64_t length);

    bool dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length);

public:
    OrderBookDispatcher();

    bool dispatch(char *buffer, int64_t offset, int64_t length);
};