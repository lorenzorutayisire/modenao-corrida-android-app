package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.upperlevel.corrida.MainActivity;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;
import org.upperlevel.corrida.phase.lobby.InsertNamePhase;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

import lombok.Getter;

public class GamePhase extends PhaseManager<InnerGamePhase> implements Phase {
    @Getter
    private Activity activity;

    @Getter
    private Game game;

    @Getter
    private CommandListener commandListener;

    public GamePhase(Activity activity, Socket socket) {
        this.activity = activity;
        game = new Game(socket);
        commandListener = new CommandListener();
    }

    @Override
    public void onLayoutSetup() {
    }

    @Override
    public void onStart() {
        Log.i("Game", "Game started");
        commandListener.start();
        setPhase(new InsertNamePhase(this));
    }

    @Override
    public void onStop() {
        Log.i("Game", "I'm stopping command listening, nou!");
        commandListener.interrupt();

        Log.i("Game", "I'm killing my only child: " + getPhase().getClass().getSimpleName());
        setPhase(null);
        try {
            Log.i("Game", "I'm deleting my remains...");
            game.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quits the current session and goes back to the main activity.
     */
    private void emergencyQuit() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish(); // calls onStop()
    }

    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            // By default, we listen for player_quit since it can happen at any time
            case "player_quit":
                Player p = game.removePlayer(cmd.args[0]);
                if (p != null) {
                    getPhase().onPlayerQuit(p);
                    Log.i("Game", "Quit player: " + cmd.args[0]);

                    if (game.getPlayers().size() == 1 && getPhase() instanceof PerformancePhase) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "Sei rimasto da solo, mi dispiace!", Toast.LENGTH_SHORT).show();
                            emergencyQuit();
                            Log.i("Game", "Bye bye, you are the only one left");
                        });
                    }
                } else {
                    Log.e("Game", "Cannot found the quit player: " + cmd.args[0]);
                }
                return true;
            default:
                Log.i("Game", "Going downward to: " + getPhase().getClass().getSimpleName());
                return getPhase().onCommandAsync(cmd);
        }
    }

    public class CommandListener extends Thread {
        @Override
        public void run() {
            Log.i("CommandCaller", "Command listener thread started");
            while (isAlive()) {
                Command cmd;
                try {
                    Log.i("Game", "Waiting for command...");
                    cmd = game.receive();
                } catch (IOException e) {
                    Log.e("Game", "Cannot read command: " + e);
                    emergencyQuit();
                    return;
                }

                Log.i("Game", "Phase: " + getPhase().getClass().getSimpleName() + " Command: " + cmd);

                if (!onCommandAsync(cmd)) {
                    Log.e("Game", "Command received but not executed: " + cmd);
                }
            }
        }
    }
}
