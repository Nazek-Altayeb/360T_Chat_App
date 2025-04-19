package Chat;

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
    private int numberOfSentMessages=1;

    public PlayerHandler(Socket socket){
        try {
            this.socket = socket;
            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(socket.getOutputStream());
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.playerName = bufferedReader.readLine();
            playerHandlers.add(this);
            //PlayerHandler initiator = playerHandlers.get(0);
            //PlayerHandler receiver = playerHandlers.get(1);
            notifyInitiator("The receiver Player: " + playerName+ " is online...");

        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage(String message){
        for(PlayerHandler playerHandler : playerHandlers)
        try{
            if(!playerHandler.playerName.equals(playerName)){
                playerHandler.bufferedWriter.write(message);
                playerHandler.bufferedWriter.newLine();
                playerHandler.bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }
    }

    public void notifyInitiator(String message){
        for(PlayerHandler playerHandler : playerHandlers)
            try{
                if(!playerHandler.playerName.equals(playerName)){
                    playerHandler.bufferedWriter.write(message);
                    playerHandler.bufferedWriter.newLine();
                    playerHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
            }
    }

    public void sendReply(String message){
        if(numberOfSentMessages <= 10){
            try{
                if(this.playerName.equals(playerHandlers.getFirst().playerName)){
                    playerHandlers.getFirst().bufferedWriter.write(message+" counter: "+numberOfSentMessages);
                    playerHandlers.getFirst().bufferedWriter.newLine();
                    playerHandlers.getFirst().bufferedWriter.flush();
                }

            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
            }
        }else{
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }
    }

    public void endChat(){
        playerHandlers.remove(this);
        endChatNotification("Initiator has sent 10 messages. Chat is terminated...");
    }

    public void closeSocketAndBuffers(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        endChat();
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

    public void endChatNotification(String message){
        for(PlayerHandler playerHandler : playerHandlers)
            try{
                    playerHandler.bufferedWriter.write(message);
                    playerHandler.bufferedWriter.newLine();
                    playerHandler.bufferedWriter.flush();

            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
            }
    }

    @Override
    public void run() {
        String message;
        while(socket.isConnected()){
            try {
                message = bufferedReader.readLine();
                sendMessage(message);
                sendReply(message);
                numberOfSentMessages++;
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
