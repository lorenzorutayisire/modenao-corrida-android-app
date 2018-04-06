package org.upperlevel.corrida.phase.lobby;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.game.Command;
import org.upperlevel.corrida.phase.game.Game;
import org.upperlevel.corrida.phase.game.GamePhase;
import org.upperlevel.corrida.phase.game.InnerGamePhase;
import org.upperlevel.corrida.phase.game.PerformancePhase;
import org.upperlevel.corrida.phase.game.Player;

import java.io.IOException;
import java.util.Arrays;

import lombok.Getter;

/**
 * This phase starts when the player has successfully registered to the server
 * and waits for other players to start. This phase listens for commands like
 * players, player_join and performance_start, to start the game. player_quit is
 * already listened by the game itself since a player can quit anywhere and not only here.
 */
public class LobbyPhase implements InnerGamePhase {
    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

    @Getter
    private TextView playersDisplay;

    public LobbyPhase(GamePhase game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.activity_wait_start);
        playersDisplay = activity.findViewById(R.id.players_count_value);
        activity.findViewById(R.id.start_game_button).setOnClickListener(new OnGameStartClick());
    }

    @Override
    public void onStop() {
    }

    private void updatePlayersDisplay() {
        int size = Game.g().getPlayers().size();
        playersDisplay.setText(size + "");
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "players":
                activity.runOnUiThread(() -> {
                    for (String name : cmd.args) {
                        Game.g().addPlayer(new Player(name));
                    }
                    Log.i("Lobby", "Added " + cmd.args.length + " players: " + Arrays.toString(cmd.args));
                    updatePlayersDisplay();
                });
                return true;
            case "player_join":
                activity.runOnUiThread(() -> {
                    Game.g().addPlayer(new Player(cmd.args[0]));
                    Log.i("Lobby", "Added player: " + cmd.args[0]);
                    updatePlayersDisplay();
                });
                return true;
            case "performance_start":
                Player performer = Game.g().getPlayer(cmd.args[0]);
                activity.runOnUiThread(() -> {
                    Log.i("Lobby", "Starting game");
                    game.setPhase(new PerformancePhase(game, performer));
                });
                return true;
        }
        return false;
    }

    public class OnGameStartClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                if (Game.g().canStart()) {
                    Game.g().emit(Command.from("request_start"));
                    Log.i("Lobby", "The game can start");
                } else {
                    Toast.makeText(activity, "Sto aspettando ulteriori giocatori!", Toast.LENGTH_SHORT).show();
                    Log.e("Lobby", "Too few players to start");
                }
            } catch (IOException e) {
                Toast.makeText(activity, "Impossibile inviare richiesta", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
