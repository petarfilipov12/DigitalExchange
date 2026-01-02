FROM azul/zulu-openjdk:21
    
WORKDIR /rest_server

COPY code .

RUN ./gradlew build -Dquarkus.package.jar.type=uber-jar

ENV REST_API_PORT=8080

WORKDIR /app

RUN cp /rest_server/build/*-runner.jar app.jar

COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]

# Run the application
ENTRYPOINT ["./entrypoint.sh"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]


