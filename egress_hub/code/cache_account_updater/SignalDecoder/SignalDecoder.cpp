#include "SignalDecoder.h"

#include "AppConfig.h"

using namespace digital::exchange::sbe::hub::inner;

std::unique_ptr<Signal> SignalDecoder::decodeSignal(char *buffer, int64_t offset, int64_t length)
{
    if(length < signalDecoder.SBE_BLOCK_LENGTH)
    {
        return nullptr;
    }

    messageHeaderDecoder.wrap(buffer, offset, messageHeaderDecoder.actingVersion(), length);

    if(messageHeaderDecoder.templateId() != signalDecoder.SBE_TEMPLATE_ID)
    {
        return nullptr;
    }

    signalDecoder.wrapForDecode(
        buffer,
        offset + messageHeaderDecoder.encodedLength(),
        messageHeaderDecoder.blockLength(),
        messageHeaderDecoder.actingVersion(),
        length
    );

    if(!(signalDecoder.hubServiceIdMask() & AppConfig::serviceId))
    {
        return nullptr;
    }

    return std::make_unique<Signal>(
        signalDecoder.hubServiceIdMask(),
        signalDecoder.position(),
        transformSignal(signalDecoder.signal())
    );
}

SignalCommandEnum SignalDecoder::transformSignal(const SignalEnum::Value signal)
{
    switch (signal)
    {
    case CONNECT:
        return SignalCommandEnum::CONNECT;

    case DISCONNECT:
        return SignalCommandEnum::DISCONNECT;

    case DO_WORK:
        return SignalCommandEnum::DO_WORK;
    
    default:
        return SignalCommandEnum::NULL_VAL;
    }
}