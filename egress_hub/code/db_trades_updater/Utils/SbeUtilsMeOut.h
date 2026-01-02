#pragma once

#include <string>

#include "digital_exchange_sbe_me_output/SbeOrderSide.h"

using namespace digital::exchange::sbe::me::output;

class SbeUtilsMeOut
{
public:
    static std::string convertSbeOrderSideToString(const SbeOrderSide::Value orderSide)
    {
        std::string res = "ORDER_SIDE_INVALID";

        switch (orderSide)
        {
        case SbeOrderSide::ORDER_SIDE_BUY:
            res = "ORDER_SIDE_BUY";
            break;
        
        case SbeOrderSide::ORDER_SIDE_SELL:
            res = "ORDER_SIDE_SELL";
            break;
        }

        return res;
    }

    static std::array<std::string, 3> getOrderSides()
    {
        return {"ORDER_SIDE_BUY", "ORDER_SIDE_SELL", "ORDER_SIDE_INVALID"};
    }
};