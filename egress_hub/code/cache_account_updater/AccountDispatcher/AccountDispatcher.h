#pragma once

#include <string>

#include "digital_exchange_sbe_me_output/MessageHeader.h"

#include "digital_exchange_sbe_me_output/MakerOrderCanceledEvent.h"
#include "digital_exchange_sbe_me_output/MakerOrderAddedEvent.h"
#include "digital_exchange_sbe_me_output/OrderFillEvent.h"

#include "digital_exchange_sbe_me_output/AddedAccountEvent.h"
#include "digital_exchange_sbe_me_output/RemovedAccountEvent.h"

#include "digital_exchange_sbe_me_output/DepositEvent.h"
#include "digital_exchange_sbe_me_output/WithdrawEvent.h"

#include "RedisAccountCacheManager.h"

class AccountDispatcher
{
private:
    digital::exchange::sbe::me::output::MessageHeader messageHeaderDecoder;

    digital::exchange::sbe::me::output::MakerOrderCanceledEvent orderCanceledEventDecoder;
    digital::exchange::sbe::me::output::MakerOrderAddedEvent orderAddedEventDecoder;
    digital::exchange::sbe::me::output::OrderFillEvent orderFillEventDecoder;

    digital::exchange::sbe::me::output::AddedAccountEvent addedAccountEventDecoder;
    digital::exchange::sbe::me::output::RemovedAccountEvent removedAccountEventDecoder;

    digital::exchange::sbe::me::output::DepositEvent depositEventDecoder;
    digital::exchange::sbe::me::output::WithdrawEvent withdrawEventDecoder;

    RedisAccountCacheManager redisManager;


    bool dispatchOrderCandeledEvent(char *buffer, int64_t offset, int64_t length);
    bool dispatchOrderAddedEvent(char *buffer, int64_t offset, int64_t length);
    bool dispatchOrderFillEvent(char *buffer, int64_t offset, int64_t length);

    bool dispatchAddedAccountEvent(char *buffer, int64_t offset, int64_t length);
    bool dispatchRemovedAccountEvent(char *buffer, int64_t offset, int64_t length);

    bool dispatchDepositEvent(char *buffer, int64_t offset, int64_t length);
    bool dispatchWithdrawEvent(char *buffer, int64_t offset, int64_t length);

public:
    AccountDispatcher();
    
    bool dispatch(char *buffer, int64_t offset, int64_t length);
};