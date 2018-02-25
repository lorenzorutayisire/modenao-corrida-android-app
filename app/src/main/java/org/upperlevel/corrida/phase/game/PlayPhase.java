package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

public class PlayPhase extends Thread implements Phase {
    public static final String TAG = "PlayPhase";

    @Getter
    private Performance performance;

    @Getter
    private Game game;

    @Getter
    private Activity activity;

    @Getter
    private TextView themeField;

    public PlayPhase(Performance performance) {
        this.performance = performance;
        this.game = performance.getGame();
        this.activity = performance.getActivity();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.play_layout);
        ((TextView) activity.findViewById(R.id.play_player_val)).setText(performance.getPerformer().getName());
        themeField = activity.findViewById(R.id.play_theme_val);
        start();
    }

    @Override
    public void onStop() {
        interrupt();
    }

    @Override
    public void run() {
        String[] command;
        // performance_theme
        Log.i(TAG, "Wait for 'performance_theme' packet");
        try {
            command = Command.split(game.receive());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (command[0].equals("performance_theme")) {
            String theme = command[1];
            Log.i(TAG, "Received performance theme: " + theme);
            performance.setTheme(theme);
            activity.runOnUiThread(() -> {
                themeField.setText(theme);
                Log.i(TAG, "Theme updated!");
            });
        }
        // rate_start
        Log.i(TAG, "Wait for 'rate_start' packet");
        try {
            command = Command.split(game.receive());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (command[0].equals("rate_start")) {
            if (!game.getMe().equals(performance.getPerformer())) {
                Log.i(TAG, "You have to rate!");
                activity.runOnUiThread(() ->
                        performance.setPhase(new RatePhase(performance)));
            } else {
                Log.i(TAG, "Performance end, you have to wait others to rate...");
                activity.runOnUiThread(() ->
                        performance.setPhase(new WaitScorePhase(performance)));
            }
        }
    }
}
