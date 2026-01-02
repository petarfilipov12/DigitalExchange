add_executable(${PROJECT_NAME} main.cpp)

# Link libraries
target_link_libraries(${PROJECT_NAME} PRIVATE
    redis_cache_manager
    redis++
    hiredis
    pthread
)