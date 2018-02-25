package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;

import lombok.Getter;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RankingPhase implements Phase {
    public static final String TAG = "Ranking";

    @Getter
    private final Game game;

    @Getter
    private final Activity activity;

    @Getter
    private LinearLayout rankingLayout;

    public RankingPhase(Game game) {
        this.game = game;
        this.activity = game.getActivity();
    }

    private void buildRanking() {
        LayoutInflater inflater = activity.getLayoutInflater();

        int position = 1;
        for (Player player : game.getRanking()) {
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

            rankingLayout.addView(view);

            position++;
        }
        Log.i(TAG, "Ranking built. Hope it's good looking.");
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.ranking_layout);

        rankingLayout = activity.findViewById(R.id.ranking_val);
        buildRanking();
    }

    @Override
    public void onStop() {
    }
}
