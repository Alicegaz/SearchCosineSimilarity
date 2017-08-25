#!/bin/sh
javac -cp ../ Main.java
java -cp ../ sample/Main "$@"