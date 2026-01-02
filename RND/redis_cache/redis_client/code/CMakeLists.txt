cmake_minimum_required(VERSION 3.12)
project(redis_client)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_subdirectory(redis_cache_manager)


add_subdirectory(main)