package Chat.SingleProcessID;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Player {
    private final BlockingQueue<String> inputQueue;
    private final BlockingQueue<String> outputQueue;
    private final ChatRoom chatroom;
    public String name;
    private int numberOfSentMessages =1;


    public Player(BlockingQueue<String> inputQueue, BlockingQueue<String> outputQueue, ChatRoom chatroom, String name) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.chatroom = chatroom;
        this.name = name;
        chatroom.addPlayer(this);
    }
    public void sendMessage(String message) throws InterruptedException {
       outputQueue.put(message);  // Add the message to the queue
    }


    public String receiveMessage() throws InterruptedException {
      return inputQueue.take();  // Take a message from the queue

    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Start the thread that reads incoming messages
    public void startReading() {
        new Thread(() -> {
            try {
                while (true) {
                    String receivedMessage = receiveMessage(); // Retrieve message from queue
                    //String receivedMessage = messageQueue.take(); // Blocks if the queue is empty
                    if (receivedMessage != null) {
                        // deliver the message to the other player
                        chatroom.broadcastMessage(this.name + " received: " + receivedMessage, this);
                        System.out.print(name + ": ");
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
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
                //System.out.print(name + ": ");
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
                    numberOfSentMessages++;
                }
                sendMessage(message);  // Add message to the queue
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            System.err.println("Reader thread interrupted");
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