#!/bin/bash

#SUBDIRS=("dbs/postgreSQL" "dbs/redis_cache" "me" "rest_server" "rest_server_marketdata" "ws_server" "frontend")

# Function to start Docker containers in given subdirectories
start_containers() {
    local dirs=("$@")  # Accept all arguments as array
    local sleep_time=1  # seconds to wait between starting containers

    for DIR in "${dirs[@]}"; do
        if [ -d "$DIR" ]; then
            echo "Starting Docker containers in $DIR..."
            (cd "$DIR" && docker compose up --build --no-start)
            if [ $? -eq 0 ]; then
                echo "Successfully started containers in $DIR"
            else
                echo "Failed to start containers in $DIR"
            fi

            echo "Sleeping for $sleep_time seconds before next directory..."
            sleep "$sleep_time"
        else
            echo "Directory $DIR does not exist, skipping..."
        fi
    done
}


subdirs=("dbs/postgreSQL" "dbs/redis_cache" "me" "egress_hub" "ingress_rest_server" "rest_server_marketdata" "ws_server" "frontend")
start_containers "${subdirs[@]}"


echo "All done."

