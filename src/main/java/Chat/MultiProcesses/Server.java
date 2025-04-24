package Chat.OneProcessID;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * listen to connection requests, those are comming from players, and respond
 * */
public class Server{
    ServerSocket serverSocket;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){

                Socket firstPlayerSocket = serverSocket.accept();
                Socket secondPlayerSocket = serverSocket.accept();

                // Start a thread to handle communication between the players
                PlayerHandler firstPlayerHandler = new PlayerHandler(firstPlayerSocket, secondPlayerSocket);
                Thread firstThread = new Thread(firstPlayerHandler);
                firstThread.start();

                System.out.println("New Player is connected : " + firstPlayerHandler.playerName+ " with a process ID : "+ firstPlayerHandler.processID);


                PlayerHandler secondPlayerHandler = new PlayerHandler(secondPlayerSocket,firstPlayerSocket);
                Thread secondThread = new Thread(secondPlayerHandler);
                secondThread.start();

                System.out.println("New Player is connected : " + secondPlayerHandler.playerName+ " with a process ID : "+ secondPlayerHandler.processID);

            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started on port: "+ 9090);
        Server server = new Server(serverSocket);
        server.startServer();
    }




}
