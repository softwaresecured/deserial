# README

This tool will output a hex, base64 encoded and a base64 encoded compress payload

The command is hardcoded in the Deserial.java file. To update, you will need to recompile.

Gradle creates a fat jar which can be used directly

###Dependencies:

1. Windows
2. Java 8

## Build

    gradle clean build

## Run

    java -jar .\build\libs\deserial-1.0.jar