package org.upperlevel.corrida.phase.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class Game {
    private static Game instance;

    @Getter
    @Setter
    private Player me = null;

    private final Map<String, Player> players = new HashMap<>();

    @Getter
    private final Socket socket;

    @Getter
    private final BufferedReader reader;

    public Game(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        instance = this; // just to simplify usage
    }

    public Player addPlayer(Player player) {
        return players.put(player.getName(), player);
    }

    public Player removePlayer(String name) {
        return players.remove(name);
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    /**
     * Gets the ranking.
     * A list of players ordered by the one with more points to the poor one.
     */
    public List<Player> getRanking() {
        List<Player> result = new ArrayList<>(getPlayers());
        boolean error;
        do {
            error = false;
            for (int i = 0; i < result.size() - 1; i++) {
                Player curr = result.get(i);
                Player next = result.get(i + 1);
                if (curr.getScore() <= next.getScore()) {
                    result.set(i, next);
                    result.set(i + 1, curr);
                    error = true;
                }
            }
        } while (error);
        return result;
    }

    /**
     * Can the game start?
     * Currently the game can start only if it has more than one player.
     */
    public boolean canStart() {
        return players.size() > 1;
    }

    public void emit(Command command) throws IOException {
        new AsyncCommandSend(socket.getOutputStream()).execute(command);
    }

    public Command receive() throws IOException {
        String line = reader.readLine();
        if (line != null) {
            return Command.fromRaw(line);
        } else {
            throw new IOException("Connection closed");
        }
    }

    public void close() throws IOException {
        reader.close();
        socket.close();

        instance = null;
    }

    /**
     * Be careful on using this. It only exists when the game is ongoing.
     */
    public static Game g() {
        return instance;
    }
}
