#pragma once

#include <memory>
#include <chrono>

#include "Aeron.h"
#include "AppConfig.h"
#include "concurrent/AtomicBuffer.h"

#include "digital_exchange_sbe_hub_inner/MessageHeader.h"
#include "digital_exchange_sbe_hub_inner/PositionUpdate.h"

class LocalResponseChannelPublication
{
private:
    static const uint8_t bufferLength = digital::exchange::sbe::hub::inner::PositionUpdate::sbeBlockAndHeaderLength();

    uint8_t buffer[bufferLength];

    digital::exchange::sbe::hub::inner::MessageHeader messageHeaderDecoder;
    digital::exchange::sbe::hub::inner::PositionUpdate positionUpdateEncoder;

    std::shared_ptr<aeron::Aeron> aeronClient;
    std::shared_ptr<aeron::ExclusivePublication> publication;

    std::shared_ptr<aeron::ExclusivePublication> addExclusivePublicationSync(
        std::shared_ptr<aeron::Aeron> aeron, 
        const std::string &channel, 
        std::int32_t streamId
    );

public:
    LocalResponseChannelPublication();

    std::int64_t sendPositionUpdate(const int64_t position);
};