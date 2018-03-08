package org.upperlevel.corrida.phase.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.command.EndJokeCommand;
import org.upperlevel.corrida.phase.Phase;

import java.io.IOException;

import lombok.Getter;

/**
 * A phase while the game waits the player to end its joke.
 */
public class EndJokePhase implements Phase {
    private static final String TAG = "EndJoke";

    @Getter
    private PlayPhase parent;

    public EndJokePhase(PlayPhase parent) {
        this.parent = parent;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "EndJokePhase started");

        Activity a = parent.getActivity();
        a.setContentView(R.layout.end_joke);
        a.findViewById(R.id.end_joke).setOnClickListener(view -> {
            try {
                parent.getGame().emit(new EndJokeCommand());
                Log.i(TAG, "EndJoke packet emitted");

                parent.setPhase(null); // end joke end
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStop() {
        Log.i(TAG, "EndJokePhase finished");
    }
}
