#!/bin/sh
./aeronmd/aeronmd.sh &
sleep 5s

echo "Starting hub_cluster_pusher\n"
java \
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
-jar /egress_hub/hub_cluster_pusher/hub_cluster_pusher-1.0-SNAPSHOT-all.jar &
sleep 3s

echo "Starting me_egress_poller\n"
java \
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
-jar /egress_hub/me_egress_poller/me_egress_poller-1.0-SNAPSHOT-all.jar &
sleep 3s

echo "Starting cache_order_book_updater\n"
./cache_order_book_updater/cache_order_book_updater &
sleep 3s

echo "Starting cache_account_updater\n"
./cache_account_updater/cache_account_updater &
sleep 3s

echo "Starting db_trades_updater\n"
./db_trades_updater/db_trades_updater &
sleep 3s

echo "Starting cluster_archive_adapter\n"
java \
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
-jar /egress_hub/cluster_archive_adapter/cluster_archive_adapter-1.0-SNAPSHOT-all.jar
