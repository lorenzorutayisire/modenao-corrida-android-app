package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.command.NameCommand;
import org.upperlevel.corrida.command.NameResponseCommand;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;
import java.util.regex.Pattern;

import lombok.Getter;

/**
 * Here we ask the player to insert its name and we send it to the player.
 */
public class InsertNamePhase implements Phase {
    public static final String TAG = "InsertNamePhase";
    private static final Pattern CHECK_NAME = Pattern.compile("^[\\s_a-zA-Z0-9]{1,16}$");

    @Getter
    private Activity activity;

    @Getter
    private Game game;

    @Getter
    private TextView nameErrorField;

    @Getter
    private Thread responseListener = null;
    private boolean listeningResponse = false;

    public InsertNamePhase(Game game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_insert_name);
        activity.findViewById(R.id.name_submit).setOnClickListener(new OnNameSubmit());
        nameErrorField = activity.findViewById(R.id.name_error);

        responseListener = new ResponseListener();
    }

    @Override
    public void onStop() {
        if (listeningResponse) {
            responseListener.interrupt();
            listeningResponse = false;
        }
    }

    /**
     * Listens for responses to the "name" packet.
     */
    public class ResponseListener extends Thread {
        @Override
        public void run() {
            listeningResponse = true;
            try {
                NameResponseCommand nameResponseCmd = new NameResponseCommand().decode(Command.split(game.receive()));
                switch (nameResponseCmd.getResponse()) {
                    case "ok":
                        activity.runOnUiThread(() -> {
                            game.setPhase(new LobbyPhase(game));
                            Log.i(TAG, "The name is perfect, going on to the next phase (LobbyPhase).");
                        });
                        break;
                    case "taken":
                        activity.runOnUiThread(() -> {
                            nameErrorField.setText("Nome già preso!");
                            Log.i(TAG, "The name has already been taken!");
                        });
                        break;
                    default:
                        Log.e(TAG, "Unknown name response: " + nameResponseCmd.getResponse());
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(activity, "Errore di ricezione", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            listeningResponse = false;
        }
    }

    /**
     * Listens for "name" send button click.
     */
    public class OnNameSubmit implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String name = ((TextView) activity.findViewById(R.id.name_input)).getText().toString();
            TextView nameError = activity.findViewById(R.id.name_error);
            if (!CHECK_NAME.matcher(name).matches()) {
                nameError.setText("Invalid syntax name!");
                return;
            }
            try {
                game.emit(new NameCommand(name));
                game.setMe(new Player(name));
                Log.i(TAG, "Name sent!");
            } catch (IOException e) {
                Toast.makeText(activity, "Cannot send name", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            if (!listeningResponse) {
                responseListener.start();
                Log.i(TAG, "Waiting for a confirmation of the name.");
            }
        }
    }
}
