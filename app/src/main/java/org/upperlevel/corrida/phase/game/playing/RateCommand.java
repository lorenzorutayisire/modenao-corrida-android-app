package org.upperlevel.corrida.phase.game.playing;

import org.upperlevel.corrida.command.Command;

import lombok.Getter;

public class RateCommand extends Command {
    @Getter
    private int rate;

    public RateCommand() {
    }

    public RateCommand(int rate) {
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
