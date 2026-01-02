FROM azul/zulu-openjdk:21

# Install build and runtime dependencies
RUN apt-get update && apt-get install -y \
    g++ \
    ca-certificates \
    git \
    wget \
    curl \
    build-essential \
    && rm -rf /var/lib/apt/lists/*
    
# Set CMake version dynamically (latest stable)
ENV CMAKE_VERSION=3.30.0
ENV CMAKE_DIR=cmake-$CMAKE_VERSION-linux-x86_64

# Download and install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v$CMAKE_VERSION/$CMAKE_DIR.tar.gz && \
    tar -xzf $CMAKE_DIR.tar.gz && \
    mv $CMAKE_DIR /opt/cmake && \
    ln -s /opt/cmake/bin/cmake /usr/local/bin/cmake && \
    rm $CMAKE_DIR.tar.gz

# Verify installation
RUN cmake --version

RUN git clone https://github.com/aeron-io/aeron.git

WORKDIR /aeron

RUN git checkout 1.48.6

RUN mkdir -p cppbuild/Release && \
	cd cppbuild/Release && \
	cmake -DCMAKE_BUILD_TYPE=Release ../.. && \
	cmake --build . --clean-first


WORKDIR /aeron_archive_proj

COPY code .

RUN ls /aeron/cppbuild/Release/lib

RUN mkdir build && cd build && cmake .. && make && mv main ..

COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]

# Run the application
ENTRYPOINT ["./entrypoint.sh"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]
