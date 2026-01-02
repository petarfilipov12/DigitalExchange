#pragma once

#include <memory>
#include <spdlog/spdlog.h>

#include "Agent.h"

#include "SbeUtils.h"
#include "MessageMappedBuffer.h"

#include "AccountDispatcher.h"

#include "SignalDecoder.h"

#include "LocalSignalChannelSubscription.h"
#include "LocalResponseChannelPublication.h"

class MainLoopAgent : public IAgent
{
private:
    std::shared_ptr<spdlog::logger> logger;
    SbeUtils sbeUtils;

    std::unique_ptr<MessageMappedBuffer> mappedBuffer_ptr = nullptr;

    AccountDispatcher accountDispatcher;

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

    bool readMessageAndPushToCache();

public: 
    MainLoopAgent();

    void onStart() override;

    int doWork() override;

    void onClose() override;

    std::string roleName() override;
};