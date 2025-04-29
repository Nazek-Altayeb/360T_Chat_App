# The Challenge description:

Having a Player class - an instance of this class with that can communicate with other Player(s) (other instances of this class)

The use case for this task is as bellow:

1. create 2 players

2. one of the players should send a message to second player (let's call this player "initiator")

3. when a player receives a message should send back a new message that contains the received message concatenated with the message counter that this player sent.

4. finalize the program (gracefully) after the initiator sent 10 messages and received back 10 messages (stop condition)

5. both players should run in the same java process (strong requirement)

6. document for every class the responsibilities it has.

7. opposite to 5: have every player in a separate JAVA process (different PID).

Please use pure Java as much as possible (no additional frameworks like spring, etc.)
Please deliver one single maven project with the source code only (no jars). Please send the maven project as archive attached to e-mail (eventual links for download will be ignored due to security policy).
Please provide a shell script to start the program.
Everything what is not clearly specified is to be decided by developer. Everything what is specified is a hard requirement.
Please focus on design and not on technology, the technology should be the simplest possible that is achieving the target.
The focus of the exercise is to deliver the cleanest and clearest design that you can achieve (and the system has to be functional).

# Solution

## Single Process 

In order to perform a chat program between two players, ensuring that both run in the same JVM process, I 
have chosen the Peer-to-Peer pattern, where the two players chat to each other bypassing a central server. 

## Design 
The two players exchange messages directly using Blocking Queues and Buffers, reading and writing run in 
different threads to avoid interfering. The system design is as follow.

![peer-to-peer](https://github.com/user-attachments/assets/395a4585-1c9d-46ec-a6fc-7bb01ea6f143)



## Different Processes 
Here, I have chosen the Client-Server pattern, it provides a centralized component, that control messages
exchanging between players, where each player runs in a different JVM process. 

## Design 
With the use of Server and Server-Socket, I have established a connection between the two players and the 
Server, each player connected to the same server port through a socket, messages are being exchanged 
through buffers, each has a different thread.

![client-server](https://github.com/user-attachments/assets/3f635797-a5e3-4c8c-bf29-445b2c9a79ca)

