#pragma once

#include <string>

namespace AppConfig
{
    /*Common Config*/
    extern std::string loggerPatern;

    extern std::string hostname;
    extern std::string wild_card_port_channel;

    /*Aeron*/
    extern std::string aeronDir;

    /* Cache Order Book Config */
    extern int serviceId;

    /* Local ME HUB Config */
    extern std::string hubInnerSignalChannel;
    extern int hubInnerSignalChannelStreamId;

    extern std::string hubInnerResponseChannel;
    extern int hubInnerResponseChannelStreamId;

    extern std::string memMappedMessageFile;

    /*Cache Account*/
    extern std::string cacheAccountUri;

} // namespace AppConfig
