/* Generated SBE (Simple Binary Encoding) message codec */
#ifndef _DIGITAL_EXCHANGE_SBE_ME_OUTPUT_SBEORDERSIDE_CXX_H_
#define _DIGITAL_EXCHANGE_SBE_ME_OUTPUT_SBEORDERSIDE_CXX_H_

#if !defined(__STDC_LIMIT_MACROS)
#  define __STDC_LIMIT_MACROS 1
#endif

#include <cstdint>
#include <iomanip>
#include <limits>
#include <ostream>
#include <stdexcept>
#include <sstream>
#include <string>

#define SBE_NULLVALUE_INT8 (std::numeric_limits<std::int8_t>::min)()
#define SBE_NULLVALUE_INT16 (std::numeric_limits<std::int16_t>::min)()
#define SBE_NULLVALUE_INT32 (std::numeric_limits<std::int32_t>::min)()
#define SBE_NULLVALUE_INT64 (std::numeric_limits<std::int64_t>::min)()
#define SBE_NULLVALUE_UINT8 (std::numeric_limits<std::uint8_t>::max)()
#define SBE_NULLVALUE_UINT16 (std::numeric_limits<std::uint16_t>::max)()
#define SBE_NULLVALUE_UINT32 (std::numeric_limits<std::uint32_t>::max)()
#define SBE_NULLVALUE_UINT64 (std::numeric_limits<std::uint64_t>::max)()

namespace digital {
namespace exchange {
namespace sbe {
namespace me {
namespace output {

class SbeOrderSide
{
public:
    enum Value
    {
        ORDER_SIDE_BUY = INT32_C(0),
        ORDER_SIDE_SELL = INT32_C(1),
        ORDER_SIDE_INVALID = INT32_C(2),
        NULL_VALUE = INT32_MIN
    };

    static SbeOrderSide::Value get(const std::int32_t value)
    {
        switch (value)
        {
            case INT32_C(0): return ORDER_SIDE_BUY;
            case INT32_C(1): return ORDER_SIDE_SELL;
            case INT32_C(2): return ORDER_SIDE_INVALID;
            case INT32_MIN: return NULL_VALUE;
        }

        throw std::runtime_error("unknown value for enum SbeOrderSide [E103]");
    }

    static const char *c_str(const SbeOrderSide::Value value)
    {
        switch (value)
        {
            case ORDER_SIDE_BUY: return "ORDER_SIDE_BUY";
            case ORDER_SIDE_SELL: return "ORDER_SIDE_SELL";
            case ORDER_SIDE_INVALID: return "ORDER_SIDE_INVALID";
            case NULL_VALUE: return "NULL_VALUE";
        }

        throw std::runtime_error("unknown value for enum SbeOrderSide [E103]:");
    }

    template<typename CharT, typename Traits>
    friend std::basic_ostream<CharT, Traits> & operator << (
        std::basic_ostream<CharT, Traits> &os, SbeOrderSide::Value m)
    {
        return os << SbeOrderSide::c_str(m);
    }
};

}
}
}
}
}

#endif
