package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Corrida;
import org.upperlevel.corrida.phase.Phase;
import org.upperlevel.corrida.phase.PhaseManager;

import lombok.Getter;

public class FetchPhase extends PhaseManager implements Phase {
    @Getter
    private Corrida parent;

    @Getter
    private Activity activity;

    public FetchPhase(Corrida parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    @Override
    public void onStart() {
        activity.setContentView(R.layout.activity_main);
        setPhase(new SelectFetchMethodPhase(this));
    }

    @Override
    public void onStop() {
    }
}
