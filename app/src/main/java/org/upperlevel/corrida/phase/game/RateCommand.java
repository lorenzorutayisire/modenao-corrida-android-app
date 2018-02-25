package org.upperlevel.corrida.phase.game;

import org.upperlevel.corrida.command.Command;

import lombok.Getter;

public class RateCommand extends Command {
    @Getter
    private float rate;

    public RateCommand() {
    }

    public RateCommand(float rate) {
        this.rate = rate;
    }

    @Override
    public String encode() {
        return "rate " + rate + "\n";
    }

    @Override
    public RateCommand decode(String[] split) {
        rate = Integer.parseInt(split[1]);
        return this;
    }
}
