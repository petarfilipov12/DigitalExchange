#pragma once

#include <memory>
#include <chrono>

#include "Aeron.h"
#include "AppConfig.h"

class LocalSignalChannelSubscription
{
private:
    std::shared_ptr<aeron::Aeron> aeronClient;
    std::shared_ptr<aeron::Subscription> subscription;
    aeron::concurrent::logbuffer::fragment_handler_t fragmentHandler;

    std::shared_ptr<aeron::Subscription> addSubscriptionSync(
        std::shared_ptr<aeron::Aeron> aeron, 
        const std::string &channel, 
        std::int32_t streamId
    );

public:
    LocalSignalChannelSubscription(aeron::concurrent::logbuffer::fragment_handler_t fragmentHandler);

    int poll();
};