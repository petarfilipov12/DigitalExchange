FROM debian:bookworm-slim

# Install only what is strictly needed to compile & run a C++ Kafka client
RUN apt-get update
RUN apt-get install -y --no-install-recommends \
    g++ \
    librdkafka-dev \
    librdkafka++1
#    && rm -rf /var/lib/apt/lists/*

WORKDIR /consumer

COPY src ./src/

RUN g++ -std=c++17 ./src/consumer.cpp -o consumer -lrdkafka++

CMD ["./consumer"]