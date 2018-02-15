package org.upperlevel.corrida.command;

public class StartRequestCommand extends Command {
    public StartRequestCommand() {
    }

    @Override
    public String encode() {
        return "start_request";
    }

    @Override
    public StartRequestCommand decode(String[] split) {
        return this;
    }
}
