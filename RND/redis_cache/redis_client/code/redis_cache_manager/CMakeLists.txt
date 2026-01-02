add_library(
    redis_cache_manager STATIC
    src/redis_cache_manager.hpp
    src/redis_cache_manager.cpp
)

target_include_directories(redis_cache_manager PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/src")