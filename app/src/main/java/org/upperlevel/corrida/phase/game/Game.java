package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;

import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Corrida;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class Game extends PhaseManager implements Phase {
    public static final String TAG = "Game";

    @Getter
    private Corrida parent;

    @Getter
    private Activity activity;

    @Getter
    private Socket socket;

    @Getter
    private BufferedReader in;

    /**
     * The player that is holding the app.
     * Must be setup!
     */
    @Getter
    @Setter
    private Player me; // The player that is currently using the app

    @Getter
    private Map<String, Player> players = new HashMap<>();

    public Game(Corrida parent, Socket socket) {
        this.parent = parent;
        this.activity = parent.getActivity();

        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get input stream", e);
        }
    }

    public void emit(Command... command) throws IOException {
        OutputStream out = socket.getOutputStream();
        new AsyncCommandSend(out).execute(command);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void addPlayer(Player player) {
        if (players.put(player.getName(), player) == null) {
            Log.i(TAG, "Added the player: \"" + player.getName() + "\" (count: " + players.size() + ").");
        } else {
            Log.w(TAG, "Replaced the player: \"" + player.getName() + "\" (count: " + players.size() + ").");
        }
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public void removePlayer(String name) {
        if (players.remove(name) != null) {
            Log.i(TAG, "Removed the player \"" + name + "\" (count: " + players.size() + ")");
        } else {
            Log.w(TAG, "Attempted to remove the player \"" + name + "\" (count: " + players.size() + "). Failed.");
        }
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    /**
     * Can the game start?
     * Currently the game can start only if it has more than 1 player.
     */
    public boolean canStart() {
        return players.size() > 1;
    }

    @Override
    public void onStart() {
        setPhase(new InsertNamePhase(this));
    }

    @Override
    public void onStop() {
        try {
            socket.close();
            Log.i(TAG, "Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
