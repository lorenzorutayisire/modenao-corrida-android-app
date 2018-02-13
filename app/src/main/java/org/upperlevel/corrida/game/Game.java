package org.upperlevel.corrida.game;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game {
    public static final String TAG = "Game";

    private Map<String, Player> players = new HashMap<>();

    public Game() {
    }

    public void addPlayer(Player player) {
        Log.i(TAG, "Adding player: " + player.getName());
        players.put(player.getName(), player);
    }

    public void removePlayer(Player player) {
        Log.i(TAG, "Removing player: " + player.getName());
        players.remove(player.getName());
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

}
