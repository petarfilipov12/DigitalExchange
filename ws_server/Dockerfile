FROM azul/zulu-openjdk:21
    
WORKDIR /ws_server_code

COPY code .

RUN ./gradlew build

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n
EXPOSE 8000

ENV AERON_DIR="/aeron"
      
RUN mkdir ${AERON_DIR}
RUN chmod -R 777 ${AERON_DIR}

WORKDIR /app

RUN cp /ws_server_code/build/libs/ws_server-1.0-SNAPSHOT-all.jar ws_server-1.0-SNAPSHOT-all.jar

COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]


# Run the application
ENTRYPOINT ["./entrypoint.sh"]
# ENTRYPOINT ["tail", "-f", "/dev/null"]

