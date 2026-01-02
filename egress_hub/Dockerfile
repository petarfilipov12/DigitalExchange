FROM azul/zulu-openjdk:21

EXPOSE 1234

# Install build and runtime dependencies
RUN apt-get update && apt-get install -y \
    g++ \
    ca-certificates \
    git \
    wget \
    curl \
    build-essential \
    libspdlog-dev \
    libfmt-dev \
    libpqxx-dev \
    libpq-dev \
    gdb \
    gdbserver \
    iputils-ping \
    vim-common \
    nano \
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

ENV MEM_MAPPED_MESSAGE_FILE="/egress_hub/temp/message.dat"

ENV AERON_DIR="/aeron_md_dir"
ENV AERON_ARCHIVE_CLUSTER_DIR="/aeron_archive_cluster_dir"
ENV AERON_CLUSTER_DIR="/aeron_cluster_dir"
      
RUN mkdir ${AERON_DIR}
RUN chmod -R 777 ${AERON_DIR}

RUN mkdir ${AERON_ARCHIVE_CLUSTER_DIR}
RUN chmod -R 777 ${AERON_ARCHIVE_CLUSTER_DIR}

RUN mkdir ${AERON_CLUSTER_DIR}
RUN chmod -R 777 ${AERON_CLUSTER_DIR}

RUN git clone https://github.com/aeron-io/aeron.git

WORKDIR /aeron

RUN git checkout 1.49.0

RUN mkdir -p cppbuild/Release && \
	cd cppbuild/Release && \
	cmake -DCMAKE_BUILD_TYPE=Release ../.. && \
	cmake --build . --clean-first

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

# Set library path
ENV LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH

WORKDIR /egress_hub

COPY code .

WORKDIR /egress_hub

RUN ["chmod", "+x", "aeronmd/aeronmd.sh"]
RUN ["chmod", "+x", "cache_order_book_updater/build.sh"]
RUN ["chmod", "+x", "cache_account_updater/build.sh"]
RUN ["chmod", "+x", "db_trades_updater/build.sh"]
RUN ["chmod", "+x", "entrypoint.sh"]

RUN ./cache_order_book_updater/build.sh
RUN ./cache_account_updater/build.sh
RUN ./db_trades_updater/build.sh

#build hub_cluster_pusher
WORKDIR /egress_hub/hub_cluster_pusher

RUN ./gradlew build
RUN cp ./build/libs/hub_cluster_pusher-1.0-SNAPSHOT-all.jar hub_cluster_pusher-1.0-SNAPSHOT-all.jar

#build me_egress_poller
WORKDIR /egress_hub/me_egress_poller

RUN ./gradlew build
RUN cp ./build/libs/me_egress_poller-1.0-SNAPSHOT-all.jar me_egress_poller-1.0-SNAPSHOT-all.jar

#build cluster_archive_adapter
WORKDIR /egress_hub/cluster_archive_adapter

RUN ./gradlew build
RUN cp ./build/libs/cluster_archive_adapter-1.0-SNAPSHOT-all.jar cluster_archive_adapter-1.0-SNAPSHOT-all.jar

#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n
#EXPOSE 8000

WORKDIR /egress_hub

# Run the application
ENTRYPOINT ["./entrypoint.sh"]



