package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.Command;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

public class BridgePhase extends Thread implements Phase {
    private static final String TAG = "BridgePhase";

    /**
     * The performance that is owning this phase.
     */
    @Getter
    private Performance performance;

    @Getter
    private Game game;

    @Getter
    private Activity activity;

    @Getter
    private float score;

    public BridgePhase(Performance performance, float score) {
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

        if (game.getMe().equals(performer)) {
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
    public void onStart() {
        activity.setContentView(R.layout.bridge_layout);
        decorLayout();
        start();
    }

    @Override
    public void onStop() {
        interrupt();
    }

    /**
     * Listens for phase change:
     * 'performance_start' -> starts a new performance
     * 'game_end' -> ends the game and shows the ranking
     */
    @Override
    public void run() {
        String[] command;
        try {
            command = Command.split(game.receive());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        switch (command[0]) {
            case "performance_start":
                Player performer = game.getPlayer(command[1]);
                activity.runOnUiThread(() -> {
                    Log.i(TAG, "New performance started! Preparing new phase...");
                    game.setPhase(new Performance(game, performer));
                });
                break;
            case "game_end":
                activity.runOnUiThread(() -> {
                    Log.i(TAG, "Game end! Preparing to show ranking...");
                    game.setPhase(new RankingPhase(game));
                });
                break;
            default:
                throw new IllegalStateException("Received unhandled packet: " + command[0]);
        }
    }
}
