package org.upperlevel.corrida.command;

import lombok.Getter;

public class PlayerJoinCommand extends Command {
    @Getter
    private String player;

    public PlayerJoinCommand() {
    }

    public PlayerJoinCommand(String player) {
        this.player = player;
    }

    @Override
    public String encode() {
        return "player_join " + player + "\n";
    }

    @Override
    public PlayerJoinCommand decode(String[] split) {
        player = split[1];
        return this;
    }

    public static PlayerJoinCommand parse(String raw) {
        PlayerJoinCommand result = new PlayerJoinCommand();
        result.decode(split(raw));
        return result;
    }
}
