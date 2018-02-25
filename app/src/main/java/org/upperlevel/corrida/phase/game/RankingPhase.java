package org.upperlevel.corrida.phase.game;

import org.upperlevel.corrida.phase.Phase;

import lombok.Getter;

public class RankingPhase implements Phase {
    @Getter
    private final Game game;

    public RankingPhase(Game game) {
        this.game = game;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }
}
