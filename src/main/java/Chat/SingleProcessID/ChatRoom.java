package Chat.SingleProcessID;
import java.io.IOException;
import java.util.ArrayList;

/*
* A class represent the area, where the written messages will be moving a round
* The two instances of Player class will join this area after creation
* */
public class ChatRoom {
    public static ArrayList<Player> players= new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    // exclude the writer from receiving his/her message
    public void broadcastMessage(String message, Player sender) throws IOException {
        for (Player player : players) {
            if (player != sender) {
                player.displayMessage(message);
            }
        }
    }

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

