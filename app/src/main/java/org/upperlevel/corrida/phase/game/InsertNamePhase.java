package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
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
    private static final Pattern CHECK_NAME = Pattern.compile("^[\\s_a-zA-Z0-9]{1,16}$");
    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

    @Getter
    private TextView nameError;

    @Getter
    private Thread responseListener = null;

    public InsertNamePhase(GamePhase game) {
        this.activity = game.getActivity();
        this.game = game;
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_insert_name);
        activity.findViewById(R.id.name_submit).setOnClickListener(new OnNameSubmit());
        nameError = activity.findViewById(R.id.name_error);
        responseListener = new ResponseListener();
    }

    @Override
    public void onStop() {
        if (responseListener.isAlive()) {
            responseListener.interrupt();
        }
    }

    public class ResponseListener extends Thread {
        @Override
        public void run() {
            try {
                switch (NameResponseCommand.parse(game.receive()).getResponse()) {
                    case OK:
                        game.setPhase(new WaitStartPhase(game));
                        break;
                    case TAKEN:
                        nameError.setText("Nome già preso!");
                        break;
                }
            } catch (IOException ignored) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Cannot receive response", Toast.LENGTH_SHORT).show());
            }
            responseListener = null;
        }
    }

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
            } catch (IOException ignored) {
                Toast.makeText(activity, "Cannot send name", Toast.LENGTH_SHORT).show();
                return;
            }
            responseListener.start();
        }
    }
}
