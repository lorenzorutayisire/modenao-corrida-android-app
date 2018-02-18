package org.upperlevel.corrida.command;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;

public class NameResponseCommand extends Command {
    @Getter
    private String response;

    public NameResponseCommand() {
    }

    public NameResponseCommand(String response) {
        this.response = response;
    }

    @Override
    public String encode() {
        return "name_response " + response + "\n";
    }

    @Override
    public NameResponseCommand decode(String[] split) {
        response = split[1];
        return this;
    }
}
