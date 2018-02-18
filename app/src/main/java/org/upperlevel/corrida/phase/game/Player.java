package org.upperlevel.corrida.phase.game;

import lombok.Getter;

public class Player {
    @Getter
    private String name;

    @Getter
    private int points;

    public Player(String name) {
        this.name = name;
        points = 0;
    }

    public void updatePoints(int points) {
        this.points = points;
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
