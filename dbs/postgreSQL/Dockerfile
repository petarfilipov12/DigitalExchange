FROM postgres:latest

COPY  init /docker-entrypoint-initdb.d/

RUN ["chmod", "-R", "755", "/docker-entrypoint-initdb.d"]

