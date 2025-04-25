#!/bin/bash

#Current_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
#javac ${Current_DIR}/*.java

#compile
javac *.java

# start server
wt.exe bash -c "java -classpath . Server.java"

sleep 3

# create the initiator (an instance of a Player Class)
wt.exe bash -c "java -classpath . Player.java"

sleep 1

# create the receiver (the second instance of the a Player Class)
wt.exe bash -c "java -classpath . Player.java"




