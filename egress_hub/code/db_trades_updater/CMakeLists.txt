cmake_minimum_required(VERSION 3.10)

# Set the project name and version
project(db_trades_updater)

# Specify C++17 standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(CMAKE_BUILD_TYPE Debug)

# Include directories (where header files are located)
include_directories(
    /aeron/aeron-client/src/main/cpp_wrapper
    /aeron/aeron-client/src/main/c
    ${CMAKE_CURRENT_SOURCE_DIR}/sbe
    ${CMAKE_CURRENT_SOURCE_DIR}/AppConfig
    ${CMAKE_CURRENT_SOURCE_DIR}/MessageMappedBuffer
    ${CMAKE_CURRENT_SOURCE_DIR}/Agent
    ${CMAKE_CURRENT_SOURCE_DIR}/Utils
    ${CMAKE_CURRENT_SOURCE_DIR}/TradesDispatcher
    ${CMAKE_CURRENT_SOURCE_DIR}/SignalDecoder
    ${CMAKE_CURRENT_SOURCE_DIR}/LocalResponseChannelPublication
    ${CMAKE_CURRENT_SOURCE_DIR}/LocalSignalChannelSubscription
    ${CMAKE_CURRENT_SOURCE_DIR}/TradesDbManager
    ${CMAKE_CURRENT_SOURCE_DIR}/MainLoopAgent
)

find_package(spdlog REQUIRED)

# Link directories (where the libraries are located)
link_directories(/aeron/cppbuild/Release/lib)

# Add your executable
add_executable(db_trades_updater  
    ${CMAKE_CURRENT_SOURCE_DIR}/AppConfig/AppConfig.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/MessageMappedBuffer/MessageMappedBuffer.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/TradesDispatcher/TradesDispatcher.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/SignalDecoder/SignalDecoder.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/LocalResponseChannelPublication/LocalResponseChannelPublication.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/LocalSignalChannelSubscription/LocalSignalChannelSubscription.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/TradesDbManager/TradesDbManager.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/MainLoopAgent/MainLoopAgent.cpp
    main.cpp
)

# Link the necessary libraries
target_link_libraries(db_trades_updater
    aeron_client
    aeron_static
    aeron_driver_static
    spdlog::spdlog
    fmt
    pqxx
    pq
    pthread
)