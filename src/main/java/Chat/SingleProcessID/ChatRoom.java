package Chat.SingleProcessID;

import Chat.MultiProcessIDs.PlayerHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class ChatRoom {
    //private final ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();
    public static ArrayList<Player> players= new ArrayList<>();

    /*public synchronized void addPlayer(Player player) {
        players.add(player);
    }*/

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void broadcastMessage(String message, Player sender) throws IOException {
        for (Player player : players) {
            if (player != sender) {
                player.displayMessage(message);
            }
        }
    }

    /*public synchronized void broadcastMessage(String message, Player sender) throws IOException {
        for (Player player : players) {
            if (player != sender) {
                player.displayMessage(message);
            }
        }
    }*/

    public String getFirstPlayerName(){
        Player player =  players.getFirst();
        String name = player.name;
        return name;
    }

    public boolean isFirstPlayer(String name){
        if(name.equals(getFirstPlayerName())){
            return true;
        }
        return false;
    }


}

