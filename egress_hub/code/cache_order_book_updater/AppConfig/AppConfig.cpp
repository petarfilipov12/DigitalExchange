#include "AppConfig.h"
#include "Utils.h"

std::string AppConfig::loggerPatern = "%H:%M:%S.%e [%l] cache_order_book_updater.%n -- %v";

std::string AppConfig::hostname = Utils::getHostname();
std::string AppConfig::wild_card_port_channel = "aeron:udp?endpoint=" + AppConfig::hostname + ":0";

/* Cache Order Book Config */
int AppConfig::serviceId = 4;

/*Aeron*/
std::string AppConfig::aeronDir = Utils::getAeronDir();

/* Local ME HUB Config */
std::string AppConfig::hubInnerSignalChannel = "aeron:ipc";
int AppConfig::hubInnerSignalChannelStreamId = 1001;

std::string AppConfig::hubInnerResponseChannel = "aeron:ipc";
int AppConfig::hubInnerResponseChannelStreamId = 1002;

std::string AppConfig::memMappedMessageFile = Utils::getMemMappedMessageFile();

/*Cache Order Book*/
std::string AppConfig::cacheOrderBookUri = "tcp://redis_node";