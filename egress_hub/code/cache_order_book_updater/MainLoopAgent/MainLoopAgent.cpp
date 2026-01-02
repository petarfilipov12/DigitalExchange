#include "MainLoopAgent.h"

#include <spdlog/sinks/stdout_color_sinks.h>

#include "AppConfig.h"

MainLoopAgent::MainLoopAgent():
signalSubsciption(
    [this](
        aeron::concurrent::AtomicBuffer &buffer,
        aeron::util::index_t offset,
        aeron::util::index_t length,
        aeron::Header &header
    )
    {
        this->fragmentHandler(buffer, offset, length, header);
    }
)
{
    logger = spdlog::stdout_color_mt("MainLoopAgent");
    logger->set_pattern(AppConfig::loggerPatern);
}

void MainLoopAgent::fragmentHandler
(
    aeron::concurrent::AtomicBuffer &buffer,
    aeron::util::index_t offset,
    aeron::util::index_t length,
    aeron::Header &header
)
{
    auto signal_ptr = signalDecoder.decodeSignal(reinterpret_cast<char*>(buffer.buffer()), offset, length);
    if(signal_ptr != nullptr)
    {
        dispatchSignal(signal_ptr->signal, signal_ptr->position);
    }
}

void MainLoopAgent::dispatchSignal(const SignalCommandEnum &signal, const int64_t position)
{
    switch (signal)
    {
    case CONNECT:
        logger->info("CONNECT");
        break;
    
    case DISCONNECT:
        logger->info("DISCONNECT");
        break;

    case DO_WORK:
        if(position > lastPosition)
        {
            if(readMessageAndPushToCache())
            {
                lastPosition = position;
                responsePublication.sendPositionUpdate(position);
            }
        }
        break;
    
    default:
        break;
    }
}

bool MainLoopAgent::readMessageAndPushToCache()
{
    if(mappedBuffer_ptr == nullptr)
    {
        mappedBuffer_ptr.reset(new MessageMappedBuffer());
    }

    sbeUtils.PrintMsg(mappedBuffer_ptr->getMappedBuffer(), MessageMappedBuffer::bufferLength);

    return orderBookDispatcher.dispatch(
        (char*)mappedBuffer_ptr->getMappedBuffer(), 
        0, 
        MessageMappedBuffer::bufferLength
    );
}

void MainLoopAgent::onStart() 
{
    logger->info("onStart");
}

int MainLoopAgent::doWork() 
{
    return signalSubsciption.poll();
}

void MainLoopAgent::onClose() 
{
    logger->info("onClose");
}

std::string MainLoopAgent::roleName() 
{
    return "MainLoopAgent";
}