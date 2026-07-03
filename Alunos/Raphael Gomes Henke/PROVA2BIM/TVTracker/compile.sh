#!/bin/bash
# Compila o projeto TV Tracker.
# Requer JDK 11 ou superior instalado (javac disponível no PATH).
set -e
cd "$(dirname "$0")"
mkdir -p out
echo "Compilando..."
javac -d out -encoding UTF-8 $(find src -name "*.java")
echo "Compilação concluída! Classes geradas em ./out"
