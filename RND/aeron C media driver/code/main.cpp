#include <iostream>
#include <thread>
#include <chrono>
#include <atomic>
#include "Aeron.h"

using namespace aeron;
using namespace aeron::util;

// Message handler called for every received fragment
void messageHandler(
    const AtomicBuffer& buffer,
    util::index_t offset,
    util::index_t length,
    const Header& header)
{
    std::string msg(reinterpret_cast<const char*>(buffer.buffer()) + offset, length);
    std::cout << "Received: " << msg << std::endl;
}

int main()
{
    std::cout << "Start" << std::endl;

    const std::string channel = "aeron:udp?endpoint=localhost:40123";
    const int streamId = 1001;

    std::atomic<bool> running{true};

    // Connect to Aeron media driver
    Context context;
    std::shared_ptr<Aeron> aeron = Aeron::connect(context);

    // Add subscription
    std::int64_t subId = aeron->addSubscription(channel, streamId);
    std::shared_ptr<Subscription> subscription = aeron->findSubscription(subId);

    // Wait until subscription is available
    while (!subscription)
    {
        std::cout << "Wait until subscription is available" << std::endl;
        std::this_thread::sleep_for(std::chrono::milliseconds(1000));
        subscription = aeron->findSubscription(subId);
    }

    std::cout << "Subscribed to " << channel << " streamId=" << streamId << std::endl;

    // Poll loop
    while (running)
    {
        // Poll up to 10 fragments at a time
        subscription->poll(messageHandler, 10);
        std::this_thread::sleep_for(std::chrono::milliseconds(10));
    }

    return 0;
}
