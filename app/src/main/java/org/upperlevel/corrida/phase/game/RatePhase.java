package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

public class RatePhase implements Phase {
    public static final String TAG = "RatePhase";

    @Getter
    private Performance performance;

    @Getter
    private Game game;

    @Getter
    private Activity activity;

    @Getter
    private RatingBar ratingBar;

    public RatePhase(Performance performance) {
        this.performance = performance;
        this.game = performance.getGame();
        this.activity = performance.getActivity();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.rate_layout);
        activity.findViewById(R.id.rate_submit).setOnClickListener(new OnSubmit());
        ratingBar = activity.findViewById(R.id.rate_ratebar);
    }

    @Override
    public void onStop() {
    }

    public class OnSubmit implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            float rating = ratingBar.getRating();
            try {
                Log.i(TAG, "Submitting rate " + rating + "...");
                game.emit(new RateCommand(rating));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            performance.setPhase(new WaitScorePhase(performance));
        }
    }
}
