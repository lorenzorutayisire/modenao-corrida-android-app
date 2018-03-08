package org.upperlevel.corrida.command;

public class EndJokeCommand extends Command {
    @Override
    public String encode() {
        return "end_joke\n";
    }

    @Override
    public EndJokeCommand decode(String[] split) {
        return new EndJokeCommand();
    }
}
