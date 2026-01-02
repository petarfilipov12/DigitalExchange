#pragma once

#include <memory>
#include <spdlog/spdlog.h>

#include "Agent.h"

#include "SbeUtilsHubInner.h"
#include "MessageMappedBuffer.h"

#include "TradesDispatcher.h"

#include "SignalDecoder.h"

#include "LocalSignalChannelSubscription.h"
#include "LocalResponseChannelPublication.h"

class MainLoopAgent : public IAgent
{
private:
    std::shared_ptr<spdlog::logger> logger;
    SbeUtilsHubInner sbeUtilsHubInner;

    std::unique_ptr<MessageMappedBuffer> mappedBuffer_ptr = nullptr;

    TradesDispatcher tradesDispatcher;

    SignalDecoder signalDecoder;

    LocalSignalChannelSubscription signalSubsciption;
    LocalResponseChannelPublication responsePublication;

    int64_t lastPosition = 0;

    void fragmentHandler(
        aeron::concurrent::AtomicBuffer &buffer,
        aeron::util::index_t offset,
        aeron::util::index_t length,
        aeron::Header &header
    );

    void dispatchSignal(const SignalCommandEnum &signal, const int64_t position);

    bool readMessageAndPushToDb();

public: 
    MainLoopAgent();

    void onStart() override;

    int doWork() override;

    void onClose() override;

    std::string roleName() override;
};