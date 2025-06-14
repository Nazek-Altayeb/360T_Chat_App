package Chat.MultiProcessIDs;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handle instances of  Players
 * */
public class PlayerHandler extends Thread{

    public static ArrayList<PlayerHandler> playerHandlers= new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String playerName;
    private int numberOfSentMessages=1;
    public Long processID;


    public PlayerHandler(Socket socket){
        try {
            this.socket = socket;
            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(socket.getOutputStream());
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.playerName = bufferedReader.readLine();
            playerHandlers.add(this);
            sendNotification("The Player: " + playerName+ " is online...", playerName);
            sendNotification("The Player: " + playerHandlers.getFirst().playerName+ " is online..." , playerHandlers.getFirst().playerName);

        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage(String message){
        if(numberOfSentMessages <= 10){
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

    }

    public void sendNotification(String message, String playername){
        for(PlayerHandler playerHandler : playerHandlers)
            try{
                if(!playerHandler.playerName.equals(playername)){
                    playerHandler.bufferedWriter.write(message);
                    playerHandler.bufferedWriter.newLine();
                    playerHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
            }
    }

    public void sendReply(String message) throws IOException {
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
            playerHandlers.getFirst().bufferedWriter.write(" You sent 10 messages and received 10 replies, Chat is terminated ... ");
            playerHandlers.getFirst().bufferedWriter.newLine();
            playerHandlers.getFirst().bufferedWriter.flush();
            endChat();
        }
    }

    public void endChat(){
        closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        sendNotification("The Initiator sent 10 messages. Chat is terminated...", playerName);
    }

    public void closeSocketAndBuffers(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        playerHandlers.remove(this);
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