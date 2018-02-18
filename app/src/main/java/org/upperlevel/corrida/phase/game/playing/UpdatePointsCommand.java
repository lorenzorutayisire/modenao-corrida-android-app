package org.upperlevel.corrida.phase.game.playing;

import org.upperlevel.corrida.command.Command;

import lombok.Getter;

public class UpdatePointsCommand extends Command {
    @Getter
    private String playerName;

    @Getter
    private int newPoints;

    public UpdatePointsCommand() {
    }

    public UpdatePointsCommand(String playerName, int newPoints) {
        this.playerName = playerName;
        this.newPoints = newPoints;
    }

    @Override
    public String encode() {
        return "update_points \"" + playerName + "\" " + newPoints + "\n";
    }

    @Override
    public UpdatePointsCommand decode(String[] split) {
        playerName = split[1];
        newPoints = Integer.parseInt(split[2]);
        return this;
    }
}
