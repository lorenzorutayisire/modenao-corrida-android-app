package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

public class WaitScorePhase extends Thread implements Phase {
    private static final String TAG = "WaitScorePhase";

    @Getter
    private Activity activity;

    @Getter
    private Game game;

    @Getter
    private Performance parent;

    public WaitScorePhase(Performance parent) {
        this.activity = parent.getActivity();
        this.game = parent.getGame();
        this.parent = parent;
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.wait_rate_layout);
        start();
    }

    @Override
    public void onStop() {
    }

    @Override
    public void run() {
        // rate_end
        String[] command;
        try {
            command = Command.split(game.receive());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (command[0].equals("rate_end")) {
            float score = Command.toFloat(command[1]);
            activity.runOnUiThread(() -> {
                Log.i(TAG, "Final rate received! Changing phase");
                parent.setPhase(new BridgePhase(parent, score));
            });
        } else {
            throw new IllegalStateException("Received unexpected command: " + command[0]);
        }
    }
}
