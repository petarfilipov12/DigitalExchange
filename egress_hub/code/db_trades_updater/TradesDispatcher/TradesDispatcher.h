#pragma once

#include <string>

#include "digital_exchange_sbe_me_output/MessageHeader.h"
#include "digital_exchange_sbe_me_output/AddedPairMatchingEngineEvent.h"
#include "digital_exchange_sbe_me_output/OrderFillEvent.h"

#include "TradesDbManager.h"

class TradesDispatcher
{
private:
    digital::exchange::sbe::me::output::MessageHeader messageHeaderDecoder;
    digital::exchange::sbe::me::output::AddedPairMatchingEngineEvent addedPairEventDecoder;
    digital::exchange::sbe::me::output::OrderFillEvent orderFillEventDecoder;

    TradesDbManager tradesDbManager;

    bool dispatchAddedPairEvent(char *buffer, int64_t offset, int64_t length);

    bool dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length);

public:
    TradesDispatcher();

    bool dispatch(char *buffer, int64_t offset, int64_t length);
};