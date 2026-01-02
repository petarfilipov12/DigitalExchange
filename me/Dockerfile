FROM azul/zulu-openjdk:21
    
WORKDIR /me_code

COPY code .

RUN ./gradlew build

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n
EXPOSE 8000

ENV AERON_DIR="/aeron"
ENV AERON_ARCHIVE_CLUSTER_DIR="/aeron_archive_cluster"
ENV AERON_CLUSTER_DIR="/aeron_cluster"
ENV AERON_ARCHIVE_EGRESS_DIR="/aeron_archive_egress"
      
RUN mkdir ${AERON_DIR}
RUN chmod -R 777 ${AERON_DIR}

RUN mkdir ${AERON_ARCHIVE_CLUSTER_DIR}
RUN chmod -R 777 ${AERON_ARCHIVE_CLUSTER_DIR}

RUN mkdir ${AERON_CLUSTER_DIR}
RUN chmod -R 777 ${AERON_CLUSTER_DIR}

RUN mkdir ${AERON_ARCHIVE_EGRESS_DIR}
RUN chmod -R 777 ${AERON_ARCHIVE_EGRESS_DIR}

WORKDIR /app

RUN cp /me_code/build/libs/me-1.0-SNAPSHOT-all.jar me-1.0-SNAPSHOT-all.jar

COPY entrypoint.sh .

RUN ["chmod", "+x", "entrypoint.sh"]


# Run the application
ENTRYPOINT ["./entrypoint.sh"]
# ENTRYPOINT ["tail", "-f", "/dev/null"]

