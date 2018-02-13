package org.upperlevel.corrida.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.CorridaApplication;
import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.command.CommandTunnel;

public class StartGameActivity extends AppCompatActivity {
    public static final String TAG = "StartGameActivity";

    private CorridaApplication application;
    private TextView playersCountValue;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        application = (CorridaApplication) getApplication();
        application.setGame(new Game()); // now we can init the game

        playersCountValue = findViewById(R.id.players_count_value);
        task = new Task();
        task.start(); // init and reads players count changes

        findViewById(R.id.start_game_button).setOnClickListener(view -> {
            application.getTunnel().send(Command.parse("start"));
            Toast.makeText(this, "Richiesta di inizio gioco inviata", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        task.interrupt();
    }

    public class Task extends Thread {
        @Override
        public void run() {
            CommandTunnel tunnel = application.getTunnel();
            Game game = application.getGame();
            while (isAlive()) {
                Log.i(TAG, "Waiting for playersCount info...");
                Command command = tunnel.read();
                String cmdName = command.getName();
                Log.i(TAG,"Command name received: " + cmdName);
                switch (cmdName) {
                    case "players":
                        Log.i(TAG, "Received players packet");
                        for (String playerName : command.getArguments()) {
                            game.addPlayer(new Player(playerName));
                        }
                        break;
                    case "player_join":
                        Log.i(TAG, "Received player join");
                        game.addPlayer(new Player(command.getArgument(0)));
                        break;
                    default:
                        Log.w(TAG, "Received an unknown command: " + command);
                        return;
                }
                runOnUiThread(() -> playersCountValue.setText(game.getPlayers().size() + ""));
            }
        }
    }
}
