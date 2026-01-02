#pragma once

#include <string>

class IAgent
{
public:
    virtual void onStart() {}

    virtual int doWork() = 0;

    virtual void onClose() {}

    virtual std::string roleName() = 0;
};