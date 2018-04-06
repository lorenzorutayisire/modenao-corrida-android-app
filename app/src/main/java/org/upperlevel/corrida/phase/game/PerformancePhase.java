package org.upperlevel.corrida.phase.game;

import android.app.Activity;

import org.upperlevel.corrida.phase.PhaseManager;

import lombok.Getter;
import lombok.Setter;

/**
 * This phase handles the whole performance from testing the player to voting him.
 * It starts when performance_start is received.
 */
public class PerformancePhase extends PhaseManager<InnerGamePhase> implements InnerGamePhase {
    @Getter
    private GamePhase game;

    @Getter
    @Setter
    private String theme; // Set in the PlayPhase

    @Getter
    private Player performer;

    @Getter
    private Activity activity;

    public PerformancePhase(GamePhase game, Player performer) {
        this.game = game;
        this.activity = game.getActivity();
        this.performer = performer;
    }

    @Override
    public void onLayoutSetup() {
    }

    @Override
    public void onStart() {
        setPhase(new PlayPhase(this));
    }

    @Override
    public void onStop() {
        setPhase(null);
    }

    @Override
    public boolean onCommandAsync(Command cmd) {
        return getPhase().onCommandAsync(cmd);
    }
}
