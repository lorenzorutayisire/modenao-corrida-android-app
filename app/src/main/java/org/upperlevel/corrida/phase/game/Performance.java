package org.upperlevel.corrida.phase.game;

import android.app.Activity;

import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;

import lombok.Getter;
import lombok.Setter;

public class Performance extends PhaseManager implements Phase {
    @Getter
    private Game game;

    @Getter
    @Setter
    private String theme; // Set in the PlayPhase

    @Getter
    private Player performer;

    @Getter
    private Activity activity;

    public Performance(Game game, Player performer) {
        this.game = game;
        this.activity = game.getActivity();
        this.performer = performer;
    }

    @Override
    public void onStart() {
        setPhase(new PlayPhase(this));
    }

    @Override
    public void onStop() {
    }
}
