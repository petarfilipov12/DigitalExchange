#include <iostream>
#include <thread>
#include <chrono>
#include <atomic>
//#include "Aeron.h"
#include "client/archive/AeronArchive.h"

using namespace aeron::archive::client;

// Message handler called for every received fragment
void messageHandler(
    const aeron::AtomicBuffer& buffer,
    aeron::util::index_t offset,
    aeron::util::index_t length,
    const aeron::Header& header)
{
    std::string msg(reinterpret_cast<const char*>(buffer.buffer()) + offset, length);
    std::cout << "Received: " << msg << std::endl;
}

int main()
{
    std::cout << "Start" << std::endl;

    Context context;
    //context.aeronDirectoryName("");
    context.controlRequestChannel("aeron:ipc");
    context.controlRequestStreamId(1);
    context.controlResponseChannel("aeron:ipc");
    context.controlResponseStreamId(2);
    std::shared_ptr<AeronArchive> aeron_archive_client = AeronArchive::connect(context);

    std::unique_ptr<RecordingDescriptor> recording_ptr;
    recording_descriptor_consumer_t consumer = [&recording_ptr](RecordingDescriptor &recordingDescriptor)
    {
        recording_ptr = std::make_unique<RecordingDescriptor>(std::move(recordingDescriptor));
    };
    aeron_archive_client->listRecordings(0, 10, consumer);

    std::cout << recording_ptr->m_recordingId;


    return 0;
}
