#include "AppConfig.h"
#include "Utils.h"

std::string AppConfig::loggerPatern = "%H:%M:%S.%e [%l] cache_account_updater.%n -- %v";

std::string AppConfig::hostname = Utils::getHostname();
std::string AppConfig::wild_card_port_channel = "aeron:udp?endpoint=" + AppConfig::hostname + ":0";

/* Cache Order Book Config */
int AppConfig::serviceId = 8;

/*Aeron*/
std::string AppConfig::aeronDir = Utils::getAeronDir();

/* Local ME HUB Config */
std::string AppConfig::hubInnerSignalChannel = "aeron:ipc";
int AppConfig::hubInnerSignalChannelStreamId = 1001;

std::string AppConfig::hubInnerResponseChannel = "aeron:ipc";
int AppConfig::hubInnerResponseChannelStreamId = 1002;

std::string AppConfig::memMappedMessageFile = Utils::getMemMappedMessageFile();

/*Cache Account*/
std::string AppConfig::cacheAccountUri = "tcp://redis_node";