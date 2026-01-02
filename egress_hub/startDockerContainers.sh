#!/bin/sh
docker compose up egress_hub_node0 &
sleep 5

docker compose up egress_hub_node1 &
sleep 5

docker compose up egress_hub_node2 &
