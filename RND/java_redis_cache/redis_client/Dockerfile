FROM azul/zulu-openjdk-debian:21

SHELL [ "/bin/bash", "-o", "pipefail", "-c" ]

# Install tools
RUN apt-get --quiet --assume-yes update &&\
    apt-get --quiet --assume-yes upgrade &&\
    apt-get install --quiet --assume-yes --no-install-suggests\
    bash\
    less\
    procps\
    sysstat\
    && apt-get clean --quiet --assume-yes &&\
    rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

WORKDIR /app

COPY untitled/build/libs/untitled-1.0-SNAPSHOT-all.jar .
COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]


# Run the application
ENTRYPOINT ["./entrypoint.sh"]
