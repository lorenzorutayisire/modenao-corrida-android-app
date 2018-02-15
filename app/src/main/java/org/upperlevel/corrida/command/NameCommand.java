package org.upperlevel.corrida.command;

import lombok.Getter;

public class NameCommand extends Command {
    @Getter
    private String name;

    public NameCommand() {
    }

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String encode() {
        return "name \"" + name + "\"\n";
    }

    @Override
    public NameCommand decode(String[] split) {
        name = split[0];
        return this;
    }
}
