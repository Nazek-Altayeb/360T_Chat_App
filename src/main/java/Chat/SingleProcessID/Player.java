package Chat.SingleProcessID;

import java.io.*;
import java.util.concurrent.*;
import java.util.Scanner;

public class Player implements  Runnable{

    public final String name;
    private final PlayerHandler playerHandler;
    private final Scanner scanner;
    BufferedReader bufferedReader ;
    BufferedWriter bufferedWriter ;
    private int numberOfSentMessages=1;

    public Player(String name, PlayerHandler playerHandler) {
        this.name = name;
        this.playerHandler = playerHandler;
        this.scanner = new Scanner(System.in);
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        playerHandler.addPlayer(this);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.print(name + ": ");
                String message = bufferedReader.readLine();
                playerHandler.broadcastMessage(name + " received: " + message, this);
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println(name + " has left the chat.");
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(String message) {
         System.out.println(message);
    }


    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        long pid = ProcessHandle.current().pid();
        System.out.println("The Process ID for both players : " + pid);

        System.out.print("Enter the name of the first player :");
        String firstPlayerName = scanner.nextLine();

        System.out.print("Enter the name of the second player :");
        String secondPlayerName = scanner.nextLine();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Create Chat
        PlayerHandler playerHandler = new PlayerHandler();

        // Create and start threads for each player
        Thread firstPlayer = new Thread(new Player(firstPlayerName, playerHandler));
        Thread secondPlayer = new Thread(new Player(secondPlayerName, playerHandler));

        // start Chat
        /*if (!firstPlayer.isAlive()) {
            firstPlayer.start();
        }
        if (!secondPlayer.isAlive()) {
            secondPlayer.start();
        }*/

        executor.submit(firstPlayer);
        executor.submit(secondPlayer);
    }
}
