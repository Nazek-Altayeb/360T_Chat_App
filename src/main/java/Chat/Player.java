package Chat;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.Scanner;

public class Player {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String playerName;


    /**
     * Handle one instance of a player
     * every instance of a player will have a name and  socket to read/write messages.
     * */
    public Player(Socket socket, String playerName) {

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.playerName = playerName;
        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * write a message a long side  with the Player name.
     * */
    public void writeMessage(){
        try{
            bufferedWriter.write(playerName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(playerName + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                receiveMessage();
            }

        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Create a new thread for received messages
     * */
    public void receiveMessage(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        while (socket.isConnected()){
                            try{
                                message = bufferedReader.readLine();
                                if(message != null){
                                    System.out.println(message);
                                }
                            }catch(IOException e){
                                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
                            }
                        }

                    }
                }
        ).start();

    }

    /**
     * Handle the IO Exception, by closing the Buffers and the socket.
     * */
    public void closeSocketAndBuffers(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(socket != null){
                socket.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{

        /*PID*/
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        String processID= jvmName.split("@")[0];
        System.out.println("PID is : "+ processID);

         Scanner scanner = new Scanner(System.in);
         System.out.println("The following is a Chat program. ");
         System.out.println("The converstion will start when both (Initiator and Receiver) players enter the chat. ");
         System.out.println("The player who is going to enter his/her name first will be the Initiator");
         System.out.print("Enter your name  : ");
         String playerName = scanner.nextLine();
         Socket socket = new Socket("localhost",9090 );
         Player player = new Player(socket, playerName);
         player.receiveMessage();
         player.writeMessage();

    }
}