#include "AppConfig.h"
#include "Utils.h"

std::string AppConfig::loggerPatern = "%H:%M:%S.%e [%l] db_trades_updater.%n -- %v";

std::string AppConfig::hostname = Utils::getHostname();
std::string AppConfig::wild_card_port_channel = "aeron:udp?endpoint=" + AppConfig::hostname + ":0";

/* Cache Order Book Config */
int AppConfig::serviceId = 16;

/*Aeron*/
std::string AppConfig::aeronDir = Utils::getAeronDir();

/* Local ME HUB Config */
std::string AppConfig::hubInnerSignalChannel = "aeron:ipc";
int AppConfig::hubInnerSignalChannelStreamId = 1001;

std::string AppConfig::hubInnerResponseChannel = "aeron:ipc";
int AppConfig::hubInnerResponseChannelStreamId = 1002;

std::string AppConfig::memMappedMessageFile = Utils::getMemMappedMessageFile();

/*Cache Order Book*/
std::string AppConfig::dbTradesHost = "postgres_node";
std::string AppConfig::dbTradesDbName = "marketdatadb";
std::string AppConfig::dbTradesUser = "admin";
std::string AppConfig::dbTradesPassword = "password";