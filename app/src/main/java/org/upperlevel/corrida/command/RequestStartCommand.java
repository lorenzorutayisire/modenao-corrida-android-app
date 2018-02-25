package org.upperlevel.corrida.command;

public class RequestStartCommand extends Command {
    public RequestStartCommand() {
    }

    @Override
    public String encode() {
        return "request_start\n";
    }

    @Override
    public RequestStartCommand decode(String[] split) {
        return this;
    }
}
