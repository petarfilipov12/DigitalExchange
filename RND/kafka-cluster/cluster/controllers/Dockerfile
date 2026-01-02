FROM apache/kafka:4.0.0

EXPOSE 9093

COPY /config /kafka/config

USER root

COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Set entrypoint
ENTRYPOINT ["/entrypoint.sh"]