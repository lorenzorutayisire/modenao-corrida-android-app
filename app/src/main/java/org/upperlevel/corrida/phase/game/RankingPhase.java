package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.lobby.LobbyPhase;

import java.io.IOException;

import lombok.Getter;

/**
 * In this phase will be shown the full ranking of all players.
 * This phase is the last of the game.
 * <p>
 * During this phase is possible to ask the game to be restarted.
 */
public class RankingPhase implements InnerGamePhase {
    @Getter
    private final GamePhase game;

    @Getter
    private final Activity activity;

    @Getter
    private LinearLayout rankingLayout;

    public RankingPhase(GamePhase game) {
        this.game = game;
        this.activity = game.getActivity();
    }

    private void buildRanking() {
        Log.i("Ranking", "Building ranking layout");
        LayoutInflater inflater = activity.getLayoutInflater();

        int position = 1;
        for (Player player : Game.g().getRanking()) {
            Log.i("Ranking", "Making line for player: " + player.getName() + " position: " + position);
            View view = inflater.inflate(R.layout.ranking_row_layout, null);

            // Position
            TextView pos = view.findViewById(R.id.ranking_row_pos);
            pos.setText(position + ".");

            // Name
            TextView pl = view.findViewById(R.id.ranking_row_player);
            pl.setText(player.getName());

            // Score
            TextView score = view.findViewById(R.id.ranking_row_score);
            score.setText(player.getScore() + "");

            Log.i("Ranking", "Adding created line to ranking layout");
            rankingLayout.addView(view);

            position++;
        }
        Log.i("Ranking", "Ranking built. Hope it's good looking");
    }


    @Override
    public void onLayoutSetup() {
        Log.i("Ranking", "Setting current view to ranking_layout");
        activity.setContentView(R.layout.ranking_layout);
        rankingLayout = activity.findViewById(R.id.ranking_val);

        activity.findViewById(R.id.restart_request).setOnClickListener(v -> {
            try {
                Game.g().emit(Command.from("request_restart"));
                Log.i("Ending", "Asking for game reset");
            } catch (IOException e) {
                Toast.makeText(activity, "Impossibile inviare richiesta", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        buildRanking();
    }

    @Override
    public void onStop() {
        Log.i("Ranking", "Ranking phase stopped");
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        switch (cmd.name) {
            case "restart":
                game.setPhase(new LobbyPhase(game));
                Log.i("Ending", "Reset request accepted, going to lobby phase");
                return true;
        }
        return false;
    }
}
