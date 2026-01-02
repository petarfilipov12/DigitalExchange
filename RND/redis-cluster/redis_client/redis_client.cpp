#include <iostream>
#include <vector>

#include <sw/redis++/redis++.h>
using namespace sw::redis;

int main() {
    try {
        //auto redis = RedisCluster("tcp://redis_node_1");
        auto redis = RedisCluster("tcp://redis_node_1");

        // Set a key
        redis.set("ClientKey", "Test123");

        // Get a key
        auto val = redis.get("ClientKey");
        if (val) {
            std::cout << *val << std::endl;
        }

        // Set a key
        redis.set("DifferentClientKey", "DifferentTest123");

        // Get a key
        val = redis.get("DifferentClientKey");
        if (val) {
            std::cout << *val << std::endl;
        }

    } catch (const Error &err) {
        std::cerr << "Redis error: " << err.what() << std::endl;
    }
}