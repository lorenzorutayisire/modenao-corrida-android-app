package org.upperlevel.corrida.phase.game;

import org.upperlevel.corrida.phase.Phase;

public interface InnerGamePhase extends Phase {

    boolean onCommandAsync(Command cmd);
}
