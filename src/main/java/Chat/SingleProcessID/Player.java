package Chat.SingleProcessID;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;

/*
* A class represent the player
* A player can read/write msg to/from queues
* the number of sent messages is considered only for the first instance of this class
* */
public class Player {
    private final BlockingQueue<String> inputQueue;
    private final BlockingQueue<String> outputQueue;
    private final ChatRoom chatroom;
    public String name;
    private int numberOfSentMessages =1;

    // Create a Player instance, and add it to the chat room.
    public Player(BlockingQueue<String> inputQueue, BlockingQueue<String> outputQueue, ChatRoom chatroom, String name) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.chatroom = chatroom;
        this.name = name;
        chatroom.addPlayer(this);
    }

    // Add the message to the queue
    public void sendMessage(String message) throws InterruptedException {
       outputQueue.put(message);
    }

    // Take a message from the queue
    public String receiveMessage() throws InterruptedException {
      return inputQueue.take();

    }

    // invoked by broadcastMessage in ChatRoom class.
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Start the thread that reads incoming messages
    public void startReading() {
        new Thread(() -> {
            try {
                while (true) {
                    if(numberOfSentMessages == 10){
                        closeQueues(inputQueue,outputQueue);
                        break;
                    }
                    String receivedMessage = receiveMessage(); // Retrieve message from queue
                    if (receivedMessage != null) {
                        chatroom.broadcastMessage(this.name + " received: " + receivedMessage, this);
                        System.out.print(name + ": ");
                    }
                }
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
                System.err.println("Reader thread interrupted");
            }

        }).start();
    }

    // Start the thread that writes user input as messages
    public void startWriting() {
        new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String message = reader.readLine();  // Wait for user input
                if (message == null) {
                    System.out.println("Received null message, skipping...");
                    continue;  // Skip null input
                }

                // Trim spaces and check for empty message
                message = message.trim();
                if (message.isEmpty()) {
                    System.out.println("Received empty message, skipping...");
                    continue;  // Skip empty input
                }

                // Log the message before sending
                System.out.println(this.name+": Writing message: \"" + message + "\"");

                boolean isFirstPlayer = chatroom.isFirstPlayer(this.name);
                if( isFirstPlayer == true){
                    System.out.println(this.name+ " sent: \"" + message + "\". counter: " + numberOfSentMessages);  // Log after sending
                    if(numberOfSentMessages == 10){
                        System.out.println("The initiator has sent 10 messages and received 10 replies, Chat is terminated");
                        closeBufferAndQueues(reader,inputQueue,outputQueue);

                        break;
                    }
                    numberOfSentMessages++;
                }
                sendMessage(message);  // Add message to the queue
            }

        } catch (IOException | InterruptedException e) {
            closeQueues(inputQueue,outputQueue);
            Thread.currentThread().interrupt();
        }
        }).start();

    }

    public void closeQueues(BlockingQueue<String> inQueue,  BlockingQueue<String> outQueue) {
        if (!inQueue.isEmpty()) {
            inQueue.clear();
        }
        if (!outQueue.isEmpty()) {
            outQueue.clear();
        }
    }

    public void closeBufferAndQueues(BufferedReader bufferedReader, BlockingQueue<String> inQueue,  BlockingQueue<String> outQueue) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (!inQueue.isEmpty()) {
                inQueue.clear();
            }
            if (!outQueue.isEmpty()) {
                outQueue.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
* once program starts, type the names of the two players.
* The two players can exchange messages in the same console, one after another.
* Chat will be terminated after the first player sends and receives ten messages
* */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();


        long pid = ProcessHandle.current().pid();
        System.out.println("The Process ID for both players : " + pid);

        System.out.print("Enter the name of the first player :");
        String firstPlayerName = scanner.nextLine();

        System.out.print("Enter the name of the second player :");
        String secondPlayerName = scanner.nextLine();

        ChatRoom chatRoom = new ChatRoom();

        Player firstPlayer = new Player(inputQueue,outputQueue,chatRoom, firstPlayerName);
        Player secondPlayer = new Player(outputQueue, inputQueue, chatRoom, secondPlayerName);

        System.out.println("In case you get an empty line while chatting, just press enter once again");
        System.out.print(firstPlayer.name + " you are the initiator, you may start : ");

        firstPlayer.startWriting();
        firstPlayer.startReading();

        secondPlayer.startReading();

        secondPlayer.startWriting();
 }
}