#include <iostream>

#include "redis_cache_manager.hpp"

int main()
{
    redis_cache_manager m("tcp://redis_node");

    m.bid_increase(100, 200);

    m.bid_decrease(100, 100);

    return 0;
    
}