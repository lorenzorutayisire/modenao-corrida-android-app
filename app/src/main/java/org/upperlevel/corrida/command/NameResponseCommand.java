package org.upperlevel.corrida.command;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;

public class NameResponseCommand extends Command {
    @Getter
    private Response response;

    public NameResponseCommand() {
    }

    public NameResponseCommand(Response response) {
        this.response = response;
    }

    @Override
    public String encode() {
        return "name_response " + response.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public NameResponseCommand decode(String[] split) {
        response = Response.MAP.get(split[1]);
        return this;
    }

    public enum Response {
        OK,
        TAKEN;

        private static Map<String, Response> MAP = new HashMap<>();

        static {
            MAP.put("ok", OK);
            MAP.put("taken", TAKEN);
        }
    }

    public static NameResponseCommand parse(String raw) {
        NameResponseCommand result = new NameResponseCommand();
        result.decode(split(raw));
        return result;
    }
}
