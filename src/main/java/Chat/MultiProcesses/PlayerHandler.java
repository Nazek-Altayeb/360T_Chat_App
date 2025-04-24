package Chat.OneProcessID;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handle instances of  Players
 * */
public class PlayerHandler implements Runnable{

    public static ArrayList<PlayerHandler> playerHandlers= new ArrayList<>();

    Socket firstPlayersocket;
    Socket secondPlayersocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public String playerName;
    private int numberOfSentMessages=1;
    public String processID;


    public PlayerHandler(Socket firstPlayersocket, Socket secondPlayersocket){
        try {
            this.firstPlayersocket = firstPlayersocket;
            this.secondPlayersocket = secondPlayersocket;
            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(secondPlayersocket.getOutputStream());
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            InputStreamReader inputStreamReader = new InputStreamReader(firstPlayersocket.getInputStream());
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.playerName = bufferedReader.readLine();
            playerHandlers.add(this);
            processID = getProcessID();
            sendNotification("The Player: " + playerName+ " is online...", playerName);
            sendNotification("The Player: " + playerHandlers.getFirst().playerName+ " is online...", playerHandlers.getFirst().playerName);

        } catch (IOException e) {
            closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
        }

    }

    private String getProcessID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
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
                    closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
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
                closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
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
                closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
            }
        }else{
            playerHandlers.getFirst().bufferedWriter.write(" You sent 10 messages and received 10 replies, Chat is terminated ... ");
            playerHandlers.getFirst().bufferedWriter.newLine();
            playerHandlers.getFirst().bufferedWriter.flush();
            endChat();
        }
    }

    public void endChat(){
        closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
        sendNotification("The Initiator sent 10 messages. Chat is terminated...", playerName);
    }

    public void closeSocketAndBuffers(Socket firstPlayersocket, Socket secondPlayersocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        playerHandlers.remove(this);
        try{
            if(firstPlayersocket != null){
                firstPlayersocket.close();
            }
            if(secondPlayersocket != null){
                secondPlayersocket.close();
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
        while(firstPlayersocket.isConnected() && secondPlayersocket.isConnected()){
            try {
                message = bufferedReader.readLine();
                sendMessage(message);
                sendReply(message);
                numberOfSentMessages++;
            } catch (IOException e) {
                closeSocketAndBuffers(firstPlayersocket, secondPlayersocket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }




}