package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.upperlevel.corrida.R;

import lombok.Getter;

/**
 * This phase shows to the player the score gained by the performer after rates.
 */
public class BridgePhase implements InnerGamePhase {
    private static final String TAG = "BridgePhase";

    @Getter
    private PerformancePhase performance;

    @Getter
    private GamePhase game;

    @Getter
    private Activity activity;

    @Getter
    private float score;

    public BridgePhase(PerformancePhase performance, float score) {
        this.performance = performance;
        this.game = performance.getGame();
        this.activity = performance.getActivity();
        this.score = score;
    }

    private void decorLayout() {
        TextView playerTxt = activity.findViewById(R.id.bridge_player);
        TextView descTxt = activity.findViewById(R.id.bridge_desc);
        TextView scoreTxt = activity.findViewById(R.id.bridge_score);

        Player performer = performance.getPerformer();

        if (Game.g().getMe().equals(performer)) {
            playerTxt.setVisibility(View.INVISIBLE);
            descTxt.setText("Hai guadagnato:");
            scoreTxt.setText(score + "");
        } else {
            playerTxt.setText(performer.getName());
            descTxt.setText("ha guadagnato:");
            scoreTxt.setText(score + "");
        }
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.bridge_layout);
        decorLayout();
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "performance_start":
                Player performer = Game.g().getPlayer(cmd.args[0]);
                activity.runOnUiThread(() -> {
                    Log.i(TAG, "New performance started! Preparing new phase...");
                    game.setPhase(new PerformancePhase(game, performer));
                });
                return true;
            case "game_end":
                activity.runOnUiThread(() -> {
                    Log.i(TAG, "Game end! Preparing to show ranking...");
                    game.setPhase(new RankingPhase(game));
                });
                return true;
            default:
                return false;
        }
    }
}
