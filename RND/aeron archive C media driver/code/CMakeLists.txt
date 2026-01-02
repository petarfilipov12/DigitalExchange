cmake_minimum_required(VERSION 3.10)

# Set the project name and version
project(AeronProject)

# Specify C++17 standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# Include directories (where header files are located)
include_directories(
    /aeron/aeron-client/src/main/cpp_wrapper
    /aeron/aeron-client/src/main/c
    /aeron/aeron-archive/src/main/cpp_wrapper
    /aeron/aeron-archive/src/main/c
)

# Link directories (where the libraries are located)
link_directories(/aeron/cppbuild/Release/lib)

# Add your executable
add_executable(main main.cpp)

# Link the necessary libraries
target_link_libraries(main
    aeron_archive_client
    aeron_archive_c_client_static
    aeron_client
    aeron_static
    aeron_driver_static
)
