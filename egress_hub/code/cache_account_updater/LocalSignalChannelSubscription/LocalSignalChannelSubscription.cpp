#include "LocalSignalChannelSubscription.h"

#include "concurrent/SleepingIdleStrategy.h"

#include <spdlog/spdlog.h>

using namespace aeron;
using namespace aeron::concurrent::logbuffer;

bool isConnected = false;

std::shared_ptr<Subscription> LocalSignalChannelSubscription::addSubscriptionSync(
    std::shared_ptr<aeron::Aeron> aeron,
    const std::string &channel,
    std::int32_t streamId)
{
    SleepingIdleStrategy idleStrategy(std::chrono::milliseconds(1));

    auto subscriptionId = aeron->addSubscription(channel, streamId);
    
    auto subscription = aeron->findSubscription(subscriptionId);
    while (!subscription)
    {
        idleStrategy.idle();
        subscription = aeron->findSubscription(subscriptionId);
    }
    
    return subscription;
}

LocalSignalChannelSubscription::LocalSignalChannelSubscription(fragment_handler_t fragmentHandler)
{
    aeron::Context context;
    context.aeronDir(AppConfig::aeronDir);
    
    aeronClient = Aeron::connect(context);

    subscription = addSubscriptionSync(aeronClient, AppConfig::hubInnerSignalChannel, AppConfig::hubInnerSignalChannelStreamId);

    this->fragmentHandler = fragmentHandler;
}

int LocalSignalChannelSubscription::poll()
{
    return subscription->poll(fragmentHandler, 1);
}