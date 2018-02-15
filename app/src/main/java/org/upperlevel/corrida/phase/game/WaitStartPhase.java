package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.command.PlayerJoinCommand;
import org.upperlevel.corrida.command.PlayerQuitCommand;
import org.upperlevel.corrida.command.PlayersCommand;
import org.upperlevel.corrida.command.StartRequestCommand;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

public class WaitStartPhase implements Phase {
    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

    @Getter
    private Thread waitStartTask; // issued when the start button is pressed

    @Getter
    private Set<String> players = Collections.synchronizedSet(new HashSet<>());

    @Getter
    private TextView playersDisplay;

    public WaitStartPhase(GamePhase game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_wait_start);
        playersDisplay = activity.findViewById(R.id.players_count_value);
        activity.findViewById(R.id.start_game_button).setOnClickListener(new OnGameStartClick());

        // just initializes tasks
        waitStartTask = new WaitStartTask();
        waitStartTask.start();
    }

    @Override
    public void onStop() {
        if (waitStartTask != null) {
            waitStartTask.interrupt();
            waitStartTask = null;
        }
    }

    public class WaitStartTask extends Thread {
        @Override
        public void run() {
            while (isAlive()) {
                String[] received;
                try {
                    received = Command.split(game.receive());
                } catch (IOException e) {
                    Toast.makeText(activity, "Errore di ricezione", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                switch (received[0]) {
                    case "players":
                        players.addAll(new PlayersCommand()
                                .decode(received)
                                .getPlayers());
                        playersDisplay.setText(players.size() + "");
                    case "player_join":
                        players.add(new PlayerJoinCommand()
                                .decode(received)
                                .getPlayer());
                        playersDisplay.setText(players.size() + "");
                        break;
                    case "player_quit":
                        players.remove(new PlayerQuitCommand()
                                .decode(received)
                                .getPlayer());
                        playersDisplay.setText(players.size() + "");
                        break;
                    case "game_start":
                        game.setPhase(new PlayingPhase());
                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, "Gioco iniziato!", Toast.LENGTH_LONG).show());
                        break;
                    default:
                        Toast.makeText(activity, "What!?", Toast.LENGTH_SHORT).show();
                        throw new IllegalStateException("Unknown packet called: " + received[0]);
                }
            }
        }
    }

    public class OnGameStartClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                game.emit(new StartRequestCommand());
            } catch (IOException e) {
                Toast.makeText(activity, "Impossibile inviare richiesta", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
