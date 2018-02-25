package org.upperlevel.corrida.phase.game;

import lombok.Getter;
import lombok.Setter;

public class Player {
    @Getter
    private String name;

    @Getter
    @Setter
    private float score;

    public Player(String name) {
        this.name = name;
        this.score = 0f;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Player) {
            return ((Player) object).name.equals(name);
        } else {
            return super.equals(object);
        }
    }
}
