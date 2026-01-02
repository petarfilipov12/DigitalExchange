#pragma once

#include <string>
#include <climits>
#include <cstdlib>
#include <unistd.h>

namespace Utils
{

    std::string getHostname()
    {
        char hostname[HOST_NAME_MAX];
        gethostname(hostname, HOST_NAME_MAX);

        return std::string(hostname);
    }

    std::string getAeronDir()
    {
        return std::string(std::getenv("AERON_DIR"));
    }

    std::string getMemMappedMessageFile()
    {
        return std::string(std::getenv("MEM_MAPPED_MESSAGE_FILE"));
    }

} // namespace Utils
