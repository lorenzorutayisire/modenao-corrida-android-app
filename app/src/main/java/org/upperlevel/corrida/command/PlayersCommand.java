package org.upperlevel.corrida.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class PlayersCommand extends Command {
    @Getter
    private Set<String> players;

    public PlayersCommand() {
    }

    public PlayersCommand(Set<String> players) {
        this.players = players;
    }

    @Override
    public String encode() {
        StringBuilder encoded = new StringBuilder("players ");
        String separator = "";
        for (String player : players) {
            encoded.append(separator).append(player);
            separator = " ";
        }
        return "players " + encoded.toString() + "\n";
    }

    @Override
    public PlayersCommand decode(String[] split) {
        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
        players = new HashSet<>();
        players.addAll(Arrays.asList(args)); // find something better if you hate me so much
        return this;
    }

    public static PlayersCommand parse(String raw) {
        PlayersCommand result = new PlayersCommand();
        result.decode(split(raw));
        return result;
    }
}
