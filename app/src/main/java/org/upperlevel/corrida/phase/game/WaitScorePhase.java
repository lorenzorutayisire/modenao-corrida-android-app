package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;

import org.upperlevel.corrida.R;

import lombok.Getter;

/**
 * In this phase the performer waits for other players ratings.
 * Should be received the packet rate_end.
 */
public class WaitScorePhase implements InnerGamePhase {
    @Getter
    private PerformancePhase performance;

    @Getter
    private Activity activity;

    @Getter
    private GamePhase game;

    public WaitScorePhase(PerformancePhase performance) {
        this.performance = performance;
        this.activity = performance.getActivity();
        this.game = performance.getGame();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.wait_rate_layout);
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "rate_end":
                Player performer = performance.getPerformer();
                float score = Float.parseFloat(cmd.args[0]); // should always be a float
                performer.setScore(score);
                Log.i("WaitScore", performer.getName() + " score: " + score);

                activity.runOnUiThread(() -> {
                    Log.i("WaitScore", "Final score of " + performer.getName() + ": " + score);
                    performance.setPhase(new BridgePhase(performance, score));
                });
                return true;
        }
        return false;
    }
}
