package org.upperlevel.corrida.phase;

import android.app.Activity;

public interface Phase {
    Activity getActivity();

    /**
     * This method must provide to setup the layout for this phase.
     */
    default void onLayoutSetup() {
    }

    /**
     * This method may be called async. Not safe to call layout functions here.
     */
    default void onStart() {
        getActivity().runOnUiThread(this::onLayoutSetup);
    }

    void onStop();
}
