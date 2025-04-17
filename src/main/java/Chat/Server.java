package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * listen to connection requests, those are comming from players, and respond
 * */
public class Server {
    ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
            }

            PlayerHandler playerHandler = new PlayerHandler(socket);
            Thread thread = new Thread(playerHandler);
            thread.start();

        } catch (IOException e) {
            closeServerSocket();
            // handle exception
            // throw new RuntimeException(e);
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

    public static  void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
