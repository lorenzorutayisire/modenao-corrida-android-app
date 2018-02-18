package org.upperlevel.corrida.command;

import lombok.Getter;

public class PlayerQuitCommand extends Command {
    @Getter
    private String player;

    public PlayerQuitCommand() {
    }

    public PlayerQuitCommand(String player) {
        this.player = player;
    }

    @Override
    public String encode() {
        return "player_quit " + player + "\n";
    }

    @Override
    public PlayerQuitCommand decode(String[] split) {
        player = split[1];
        return this;
    }

    public static PlayerQuitCommand parse(String raw) {
        PlayerQuitCommand result = new PlayerQuitCommand();
        result.decode(split(raw));
        return result;
    }
}
