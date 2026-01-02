#!/bin/bash
set -e

CONFIG_FILE="/kafka/config/config.properties"

# Ensure advertised.listeners is set from environment variable
if grep -q '^advertised.listeners=' "$CONFIG_FILE"; then
    sed -i "s|^advertised.listeners=.*|advertised.listeners=$ADVERTISED_LISTENERS|" "$CONFIG_FILE"
else
    echo "advertised.listeners=$ADVERTISED_LISTENERS" >> "$CONFIG_FILE"
fi

# Ensure node.id is set from environment variable
if grep -q '^node.id=' "$CONFIG_FILE"; then
    sed -i "s|^node.id=.*|node.id=$NODE_ID|" "$CONFIG_FILE"
else
    echo "node.id=$NODE_ID" >> "$CONFIG_FILE"
fi

# Extract cluster.id from the config file
CLUSTER_ID=$(grep '^cluster.id=' "$CONFIG_FILE" | cut -d'=' -f2)

# Format Kafka storage (ignore if already formatted)
echo "Formatting Kafka storage (if not already formatted)..."
/opt/kafka/bin/kafka-storage.sh format \
    --config "$CONFIG_FILE" \
    --cluster-id "$CLUSTER_ID" \
    --ignore-formatted

# Start Kafka broker
echo Start Kafka broker
exec /opt/kafka/bin/kafka-server-start.sh "$CONFIG_FILE"