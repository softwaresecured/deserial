# README - Deserial

This tool will output a hex, a base64 encoded and a base64 encoded compressed payload.

Gradle creates a fat jar which can be used directly

###Dependencies:

1. Windows
2. Java 8

## Build

    gradle clean fatJar

## Run

    java -jar .\build\libs\deserial-1.0-all.jar -help

    java -jar .\build\libs\deserial-1.0-all.jar -command calc.exe -payload CommonsBeanutils1 -output base64