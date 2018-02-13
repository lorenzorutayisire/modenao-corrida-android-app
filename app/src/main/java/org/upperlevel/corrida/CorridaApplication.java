package org.upperlevel.corrida;

import android.app.Application;

import org.upperlevel.corrida.command.CommandTunnel;
import org.upperlevel.corrida.game.Game;

import java.io.IOException;

public class CorridaApplication extends Application {
    private CommandTunnel connection;
    private Game game;

    public void reset() {
        if (connection != null) {
            try {
                connection.getSocket().close();
            } catch (IOException ignored) {
            }
            connection = null;
        }
        if (game != null) {
            game = null;
        }
    }

    public CommandTunnel getTunnel() {
        return connection;
    }

    public void setTunnel(CommandTunnel connection) {
        this.connection = connection;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
