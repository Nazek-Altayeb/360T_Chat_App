#!/bin/bash

#Current_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
#javac ${Current_DIR}/*.java
#java ${Current_DIR}/Server



# start server
./server.sh &

# sleep 3
# Start the client
#./player.sh

# execute the previous command in a new shell window
# gnome-terminal -- bash -c "!!; exec bash"
#gnome-terminal -e "bash -c 'echo hello world; sleep 3'"


#gnome-terminal --tab --title="test" --command="bash -c 'cd /player.sh; ls; $SHELL'"


wt.exe bash -c "java -classpath . Player.java"





# List of commands to run
#commands=(
 # "echo 'Running command 1'; sleep 5"
  #"echo 'Running command 2'; sleep 5"
  #"echo 'Running command 3'; sleep 5"
#)

# Loop through each command and open it in a new terminal
#for cmd in "${commands[@]}"; do
 # gnome-terminal -- bash -c "$cmd; exec bash" &
#done


