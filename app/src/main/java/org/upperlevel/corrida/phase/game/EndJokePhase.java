package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

/**
 * A phase while the game waits the player to end its joke.
 */
public class EndJokePhase implements InnerGamePhase {
    @Getter
    private Activity activity;

    @Getter
    private PlayPhase parent;

    public EndJokePhase(PlayPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    @Override
    public void onLayoutSetup() {
        activity.setContentView(R.layout.end_joke);
        activity.findViewById(R.id.end_joke).setOnClickListener(view -> {
            try {
                Game.g().emit(Command.from("end_joke"));
                Log.i("EndJoke", "Joke end");

                parent.setPhase(null); // ends this phase
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        return false;
    }
}
