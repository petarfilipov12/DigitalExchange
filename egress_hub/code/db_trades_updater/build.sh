#!/bin/sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd ${SCRIPT_DIR}
mkdir build
cd build
cmake ..
make
mv db_trades_updater ..
