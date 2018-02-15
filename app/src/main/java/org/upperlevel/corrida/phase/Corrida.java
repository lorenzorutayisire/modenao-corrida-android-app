package org.upperlevel.corrida.phase;

import android.app.Activity;

import org.upperlevel.corrida.phase.fetch.FetchPhase;

import lombok.Getter;

public class Corrida extends PhaseManager {
    @Getter
    private Activity activity;

    public Corrida(Activity activity) {
        this.activity = activity;
    }

    public void start() {
        setPhase(new FetchPhase(this));
    }
}
