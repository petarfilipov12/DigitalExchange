#pragma once

#include <cstdint>

class MessageMappedBuffer
{
private:
    void* buffer = nullptr;
    int fd = -1;

public:
    static const uint64_t bufferLength = 256;

    MessageMappedBuffer();
    ~MessageMappedBuffer();

    void* getMappedBuffer();
};