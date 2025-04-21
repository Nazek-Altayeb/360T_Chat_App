package Chat;

import java.io.IOException;
import java.lang.management.ManagementFactory;
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
                Socket socket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(socket);
                Thread thread = new Thread(playerHandler);
                thread.start();
                System.out.println("New Player is connected : " + playerHandler.playerName);
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
