#include "LocalResponseChannelPublication.h"

#include <cstring>

#include "AppConfig.h"
#include "concurrent/SleepingIdleStrategy.h"

using namespace aeron;

std::shared_ptr<ExclusivePublication> LocalResponseChannelPublication::addExclusivePublicationSync(
    std::shared_ptr<Aeron> aeron, 
    const std::string &channel, 
    std::int32_t streamId
)
{
    SleepingIdleStrategy idleStrategy(std::chrono::milliseconds(1));
    
    auto publicationId = aeron->addExclusivePublication(channel, streamId);
    
    auto publication = aeron->findExclusivePublication(publicationId);
    while (!publication)
    {
        idleStrategy.idle();
        publication = aeron->findExclusivePublication(publicationId);
    }

    return publication;
}

LocalResponseChannelPublication::LocalResponseChannelPublication()
{
    aeron::Context context;
    context.aeronDir(AppConfig::aeronDir);

    aeronClient = Aeron::connect(context);

    publication = addExclusivePublicationSync(
        aeronClient, 
        AppConfig::hubInnerResponseChannel, 
        AppConfig::hubInnerResponseChannelStreamId
    );

    // std::memset(buffer, 0, bufferLength);

    // messageHeaderDecoder.wrap(buffer, 0, PositionUpdate::SBE_SCHEMA_VERSION, sizeof(buffer)/sizeof(buffer[0]))
    //     .blockLength(PositionUpdate::SBE_BLOCK_LENGTH)
    //     .templateId(PositionUpdate::SBE_TEMPLATE_ID)
    //     .schemaId(PositionUpdate::SBE_SCHEMA_ID)
    //     .version(PositionUpdate::SBE_SCHEMA_VERSION);

    positionUpdateEncoder.wrapAndApplyHeader(reinterpret_cast<char*>(buffer), 0, bufferLength);
    positionUpdateEncoder.hubServiceId(AppConfig::serviceId);
}

std::int64_t LocalResponseChannelPublication::sendPositionUpdate(const int64_t position)
{
    positionUpdateEncoder.position(position);

    return publication->offer(buffer, bufferLength);
}
