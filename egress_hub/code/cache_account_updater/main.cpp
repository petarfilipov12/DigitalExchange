#include <iostream>
#include <chrono>
#include <stdexcept>

#include "concurrent/SleepingIdleStrategy.h"

#include "MainLoopAgent.h"

using namespace aeron::concurrent;


int main()
{
    MainLoopAgent mainLoopAgent;
    SleepingIdleStrategy idleStrategy(std::chrono::milliseconds(1));

    try
    {
        mainLoopAgent.onStart();
        while(true)
        {
            idleStrategy.idle(mainLoopAgent.doWork());
        }
    }
    catch(const std::exception& e)
    {
        throw std::runtime_error(e.what());
        mainLoopAgent.onClose();

        return -1;
    }

    return 0;
}