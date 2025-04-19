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

    public PlayerHandler(Socket socket){
        try {
            this.socket = socket;

            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(socket.getOutputStream());
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.playerName = bufferedReader.readLine();
            //sendMessage("The Player: " + playerName+ " is online...");
            //sendPlayerName("The Player: " + playerName+ " is online...");
            playerHandlers.add(this);
            //PlayerHandler initiator = playerHandlers.get(0);
            //PlayerHandler receiver = playerHandlers.get(1);

            /**if(receiver.playerName != null){

                sendMessage("The receiver player: " + receiver.playerName+ " is online...");
            }

            if(initiator.playerName != null){

                sendMessage("The initiator player: " + initiator.playerName+ " is online...");
            }*/

            notify("The receiver Player: " + playerName+ " is online...");
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

    public void notify(String message){
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

        try{
            if(this.playerName.equals(playerHandlers.getFirst().playerName)){
                playerHandlers.getFirst().bufferedWriter.write(message);
                playerHandlers.getFirst().bufferedWriter.newLine();
                playerHandlers.getFirst().bufferedWriter.flush();
            }

        } catch (IOException e) {
            closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        }
    }

    public void stopChat(){
        playerHandlers.remove(this);
        closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
        // sendMessage("The initiator sent 10 messages and got 10 replies, Chat is ended...");
    }

    public void closeSocketAndBuffers(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        stopChat();
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
            } catch (IOException e) {
                closeSocketAndBuffers(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
