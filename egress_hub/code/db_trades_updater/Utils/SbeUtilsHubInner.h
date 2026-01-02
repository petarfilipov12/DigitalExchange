#pragma once

#include <cstdint>
#include <memory>
#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>

#include <iostream>
#include <iomanip>
#include <iterator>

#include "AppConfig.h"

#include "digital_exchange_sbe_hub_inner/MessageHeader.h"

using namespace digital::exchange::sbe::hub::inner;

class SbeUtilsHubInner
{
private:
    std::shared_ptr<spdlog::logger> logger;
    MessageHeader messageHeaderDecoder;

public:
    SbeUtilsHubInner()
    {
        logger = spdlog::stdout_color_mt("SbeUtilsHubInner");
        logger->set_pattern(AppConfig::loggerPatern);
    }

    void PrintMsg(void* buffer, const uint64_t length)
    {
        if(length < MessageHeader::encodedLength())
        {
            logger->warn("Message too short, skipping: {}", length);
        }

        messageHeaderDecoder.wrap((char*)buffer, 0, MessageHeader::sbeSchemaVersion(), length);

        logger->info("New Message, Template Id: {}", messageHeaderDecoder.templateId());

        uint8_t* buff = (uint8_t*)buffer;
        std::cout << std::hex << std::setw(2) << std::setfill('0');
        std::copy(buff, buff + length, std::ostream_iterator<int>(std::cout, " "));
        std::cout << std::endl;
    }
};
