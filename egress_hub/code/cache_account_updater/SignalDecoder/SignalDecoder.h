#pragma once

#include <memory>

#include "digital_exchange_sbe_hub_inner/MessageHeader.h"
#include "digital_exchange_sbe_hub_inner/SignalEnumFromHubNode.h"

enum SignalCommandEnum
{
    CONNECT,
    DISCONNECT,
    DO_WORK,
    NULL_VAL
};

struct Signal
{
    const int32_t hubServiceIdMask;
    const int64_t position;
    const SignalCommandEnum signal;

    Signal(const int32_t hubServiceIdMask, const int64_t position, const SignalCommandEnum signal):
    hubServiceIdMask(hubServiceIdMask), position(position), signal(signal)
    {}
};

class SignalDecoder
{
private:
    digital::exchange::sbe::hub::inner::MessageHeader messageHeaderDecoder;
    digital::exchange::sbe::hub::inner::SignalEnumFromHubNode signalDecoder;

public:
    std::unique_ptr<Signal> decodeSignal(char *buffer, int64_t offset, int64_t length);

    SignalCommandEnum transformSignal(const digital::exchange::sbe::hub::inner::SignalEnum::Value signal);
};
