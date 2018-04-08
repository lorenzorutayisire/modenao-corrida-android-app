package org.upperlevel.corrida.phase.game;

import org.upperlevel.corrida.phase.Phase;

public interface InnerGamePhase extends Phase {

    default void onPlayerQuit(Player player) {
    }

    boolean onCommandAsync(Command cmd);
}
