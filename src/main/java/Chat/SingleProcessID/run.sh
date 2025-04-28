#!/bin/bash


#compile
javac *.java

#run program
wt.exe bash -c "java -classpath . Player.java"