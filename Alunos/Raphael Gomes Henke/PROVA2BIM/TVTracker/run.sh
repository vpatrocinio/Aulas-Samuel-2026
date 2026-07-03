#!/bin/bash
# Executa o TV Tracker (compile.sh deve ter sido rodado antes).
set -e
cd "$(dirname "$0")"
if [ ! -d "out" ]; then
    echo "Pasta 'out' não encontrada. Rodando compile.sh primeiro..."
    ./compile.sh
fi
java -cp out com.tvtracker.Main
