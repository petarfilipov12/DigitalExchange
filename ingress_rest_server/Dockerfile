FROM azul/zulu-openjdk:21
    
WORKDIR /rest_server

COPY code .

RUN ./gradlew build -Dquarkus.package.jar.type=uber-jar

EXPOSE 29000
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:29000

ENV REST_API_PORT=8080
ENV AERON_DIR="/aeron_md_dir"

WORKDIR /app

RUN cp /rest_server/build/*-runner.jar app.jar

COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]

# Run the application
ENTRYPOINT ["./entrypoint.sh"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]


