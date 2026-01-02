#include "MessageMappedBuffer.h"

#include <fcntl.h>
#include <unistd.h>
#include <stdexcept>
#include <sys/mman.h>

#include "AppConfig.h"

MessageMappedBuffer::MessageMappedBuffer()
{
    fd = open(AppConfig::memMappedMessageFile.c_str(), O_RDONLY);
    if(fd < 0) throw std::runtime_error("Failed to open file " + AppConfig::memMappedMessageFile);

    buffer = mmap(nullptr, bufferLength, PROT_READ, MAP_SHARED, fd, 0);
    if(buffer == MAP_FAILED) throw std::runtime_error("Failed to mmap " + AppConfig::memMappedMessageFile);
}

MessageMappedBuffer::~MessageMappedBuffer()
{
    munmap(buffer, bufferLength);
    close(fd);
}

void* MessageMappedBuffer::getMappedBuffer()
{
    return buffer;
}