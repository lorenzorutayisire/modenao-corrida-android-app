package org.upperlevel.corrida.phase.fetch;

import android.app.Activity;

import org.upperlevel.corrida.R;
import org.upperlevel.corrida.phase.Phase;

public class SelectFetchMethodPhase implements Phase{
    private FetchPhase parent;
    private Activity activity;

    public SelectFetchMethodPhase(FetchPhase parent) {
        this.parent = parent;
        this.activity = parent.getActivity();
    }

    @Override
    public void onStart() {
        activity.findViewById(R.id.search_nao_button).setOnClickListener(view -> {
            parent.setPhase(new SearchNaoPhase(parent));
        });
        activity.findViewById(R.id.join_nao_button).setOnClickListener(view -> {
            parent.setPhase(new JoinNaoPhase(parent));
        });
    }

    @Override
    public void onStop() {
    }
}
