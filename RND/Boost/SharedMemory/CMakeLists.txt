cmake_minimum_required(VERSION 3.10)

project(BoostProj)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE "Debug")
set(CMAKE_CXX_FLAGS_DEBUG "-g")

include_directories(${CMAKE_SOURCE_DIR})

add_executable(writer writer.cpp)

add_executable(reader reader.cpp)
