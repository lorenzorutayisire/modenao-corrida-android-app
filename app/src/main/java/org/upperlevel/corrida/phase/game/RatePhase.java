package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;

import org.upperlevel.corrida.R;

import java.io.IOException;

import lombok.Getter;

/**
 * During this phase the player can vote the performance of another.
 * This phase listens for no packet, just sends the rating.
 */
public class RatePhase implements InnerGamePhase {
    @Getter
    private PerformancePhase performance;

    @Getter
    private GamePhase game;

    @Getter
    private Activity activity;

    @Getter
    private RatingBar ratingBar;

    public RatePhase(PerformancePhase performance) {
        this.performance = performance;
        this.game = performance.getGame();
        this.activity = performance.getActivity();
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.rate_layout);
        activity.findViewById(R.id.rate_submit).setOnClickListener(new OnSubmit());
        ratingBar = activity.findViewById(R.id.rate_ratebar);
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        return false;
    }

    public class OnSubmit implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            float rating = ratingBar.getRating();
            try {
                Log.i("Rate", "Submitting rating: " + rating);
                Game.g().emit(Command.from("rate", rating));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            performance.setPhase(new WaitScorePhase(performance));
        }
    }
}
