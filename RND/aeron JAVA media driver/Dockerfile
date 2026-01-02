FROM azul/zulu-openjdk-debian:21

# Install build and runtime dependencies
RUN apt-get update && apt-get upgrade && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR ./aeron

RUN curl -L -o aeron-all-1.48.6.jar \
  https://repo1.maven.org/maven2/io/aeron/aeron-all/1.48.6/aeron-all-1.48.6.jar
  
COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]


# Run the application
ENTRYPOINT ["./entrypoint.sh"]
