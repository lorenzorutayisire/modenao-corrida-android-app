package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.PhaseManager;

import lombok.Getter;

/**
 * This phase starts when the performance_start packet has received.
 * It waits for performance information such as theme and rate_start.
 */
public class PlayPhase extends PhaseManager<InnerGamePhase> implements InnerGamePhase {
    @Getter
    private PerformancePhase performance;

    @Getter
    private GamePhase game;

    @Getter
    private Activity activity;

    @Getter
    private TextView themeField;

    public PlayPhase(PerformancePhase performance) {
        this.performance = performance;
        this.game = performance.getGame();
        this.activity = performance.getActivity();
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.play_layout);
        ((TextView) activity.findViewById(R.id.play_player_val)).setText(performance.getPerformer().getName());
        themeField = activity.findViewById(R.id.play_theme_val);
    }

    @Override
    public void onStop() {
        setPhase(null);
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "performance_theme":
                String theme = cmd.args[0];
                performance.setTheme(theme);
                activity.runOnUiThread(() -> {
                    themeField.setText(theme);
                    Log.i("Performance", "Theme updated");

                    if (theme.equals("battuta") && Game.g().getMe().equals(performance.getPerformer())) {
                        setPhase(new EndJokePhase(PlayPhase.this));
                    }
                });
                return true;
            case "rate_start":
                if (!Game.g().getMe().equals(performance.getPerformer())) {
                    activity.runOnUiThread(() -> {
                        Log.i("Performance", "Now players should rate");
                        performance.setPhase(new RatePhase(performance));
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        Log.i("Performance", "PerformancePhase end. Waiting for ratings");
                        performance.setPhase(new WaitScorePhase(performance));
                    });
                }
                return true;
            default:
                return getPhase().onCommandAsync(cmd); // useless
        }
    }
}
