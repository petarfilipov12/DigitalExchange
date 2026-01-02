FROM debian:stable-slim

# Install build and runtime dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    g++ \
    ca-certificates \
    git \
    cmake \
    build-essential \
    libssl-dev \
    libhiredis-dev \
    libstdc++6 \
    libgcc1 \
    && rm -rf /var/lib/apt/lists/*

# Install hiredis (optional, already installed via libhiredis-dev)
WORKDIR /libs
RUN git clone https://github.com/redis/hiredis.git && \
    cd hiredis && \
    make && make install

# Install redis-plus-plus
RUN git clone https://github.com/sewenew/redis-plus-plus.git && \
    cd redis-plus-plus && \
    mkdir build && cd build && \
    cmake .. && make && make install

# Copy your single C++ file
WORKDIR /app
COPY redis_client.cpp .

# Compile your single-file app
RUN g++ -std=c++17 -o redis_client redis_client.cpp -lredis++ -lhiredis -pthread

# Set library path
ENV LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH

# Run the application
CMD ["./redis_client"]
