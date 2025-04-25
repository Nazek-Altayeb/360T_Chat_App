package Chat.SingleProcessID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerHandler {
    private final ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();

    public synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public synchronized void broadcastMessage(String message, Player sender) throws IOException {
        for (Player player : players) {
            if (player != sender) {
                player.receiveMessage(message);
            }
        }
    }
}
