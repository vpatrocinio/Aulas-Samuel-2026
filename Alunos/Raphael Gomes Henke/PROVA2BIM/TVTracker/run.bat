@echo off
REM Compila e executa o TV Tracker no Windows.
REM Requer JDK 11 ou superior instalado (javac e java no PATH).

if not exist out mkdir out

echo Compilando...
dir /s /b src\*.java > sources.txt
javac -d out -encoding UTF-8 @sources.txt
del sources.txt

echo Executando...
java -cp out com.tvtracker.Main

pause
