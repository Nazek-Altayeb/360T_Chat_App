package Chat;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handle instances of  Players
 * */
public class PlayerHandler implements Runnable{

    public static ArrayList<PlayerHandler> playerHandlers= new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String playerName;

    public PlayerHandler(@NotNull Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.playerName = bufferedReader.readLine();
            playerHandlers.add(this);

            sendMessage("The Player: " + playerName+ " is online...");
        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage(String message){
        for(PlayerHandler playerHandler : playerHandlers) {
            try {
               if (!playerHandler.playerName.equals(this.playerName)) {
                    playerHandler.bufferedWriter.write(message);
                    playerHandler.bufferedWriter.newLine();
                    playerHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void exitChat(){
        playerHandlers.remove(this);
        sendMessage("The initiator sent 10 messages and got 10 replies, Chat is ended...");
    }

    public void closeSocketAndBuffers(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        exitChat();
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

    @Override
    public void run() {
        String messageFromFirstPlayer;
        while(socket.isConnected()){
            try {
                messageFromFirstPlayer = bufferedReader.readLine();
                sendMessage(messageFromFirstPlayer);
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
