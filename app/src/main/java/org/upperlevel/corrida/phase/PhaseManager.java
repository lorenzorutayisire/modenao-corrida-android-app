package org.upperlevel.corrida.phase;

import android.util.Log;

import lombok.Getter;

public class PhaseManager<P extends Phase> {
    private P phase;

    public synchronized P getPhase() {
        return phase;
    }

    public synchronized void setPhase(P phase) {
        Phase old = this.phase;
        if (old != null) {
            old.onStop();
        }
        this.phase = phase;
        if (phase != null) {
            Log.i("PhaseManager", "Calling " + phase.getClass().getSimpleName() + "#onStart()");
            phase.onStart();
        }
    }
}
