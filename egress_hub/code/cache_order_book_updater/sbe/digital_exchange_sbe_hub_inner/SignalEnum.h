/* Generated SBE (Simple Binary Encoding) message codec */
#ifndef _DIGITAL_EXCHANGE_SBE_HUB_INNER_SIGNALENUM_CXX_H_
#define _DIGITAL_EXCHANGE_SBE_HUB_INNER_SIGNALENUM_CXX_H_

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
namespace hub {
namespace inner {

class SignalEnum
{
public:
    enum Value
    {
        CONNECT = INT32_C(0),
        DISCONNECT = INT32_C(1),
        DO_WORK = INT32_C(2),
        NULL_VALUE = INT32_MIN
    };

    static SignalEnum::Value get(const std::int32_t value)
    {
        switch (value)
        {
            case INT32_C(0): return CONNECT;
            case INT32_C(1): return DISCONNECT;
            case INT32_C(2): return DO_WORK;
            case INT32_MIN: return NULL_VALUE;
        }

        throw std::runtime_error("unknown value for enum SignalEnum [E103]");
    }

    static const char *c_str(const SignalEnum::Value value)
    {
        switch (value)
        {
            case CONNECT: return "CONNECT";
            case DISCONNECT: return "DISCONNECT";
            case DO_WORK: return "DO_WORK";
            case NULL_VALUE: return "NULL_VALUE";
        }

        throw std::runtime_error("unknown value for enum SignalEnum [E103]:");
    }

    template<typename CharT, typename Traits>
    friend std::basic_ostream<CharT, Traits> & operator << (
        std::basic_ostream<CharT, Traits> &os, SignalEnum::Value m)
    {
        return os << SignalEnum::c_str(m);
    }
};

}
}
}
}
}

#endif
