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
import org.upperlevel.corrida.phase.game.Player;

import java.io.IOException;
import java.util.regex.Pattern;

import lombok.Getter;

public class InsertNamePhase implements InnerGamePhase {
    private static final Pattern CHECK_NAME = Pattern.compile("^[\\s_a-zA-Z0-9]{1,16}$");

    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

    @Getter
    private TextView nameErrorField;

    public InsertNamePhase(GamePhase game) {
        this.game = game;
        this.activity = game.getActivity();
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.activity_insert_name);
        activity.findViewById(R.id.name_submit).setOnClickListener(new OnNameSubmit());
        nameErrorField = activity.findViewById(R.id.name_error);
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "name_response":
                switch (cmd.args[0]) {
                    case "ok":
                        Log.i("InsertName", "Beautiful name. Going to lobby");
                        game.setPhase(new LobbyPhase(game));
                        break;
                    case "taken":
                        activity.runOnUiThread(() -> {
                            Log.i("InsertName", "Name already taken");
                            nameErrorField.setText("Nome già preso!");
                        });
                        break;
                    default:
                        Log.e("InsertName", "Unknown name response: " + cmd.args[0]);
                        break;
                }
                return true;
        }
        return false;
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
                Game.g().emit(Command.from("name", name));
                Game.g().setMe(new Player(name));
                Log.i("InsertName", "Name sent");
            } catch (IOException e) {
                Toast.makeText(activity, "Cannot send name", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
