package org.upperlevel.corrida.phase.game;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class Command {
    public static final Pattern PATTERN = Pattern.compile("([^\\s\"]+)|(?:\"(.+?[^\\\\])\")");

    @Getter
    public final String name;

    @Getter
    public final String[] args;

    private Command(String name, String... args) {
        this.name = name;
        this.args = args;
    }

    public byte[] getPacket() {
        return (toString() + "\n").getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(name);
        for (Object arg : args) {
            res.append(" ");
            String inner = arg.toString();
            if (inner.contains(" ")) {
                inner = "\"" + inner + "\"";
            }
            res.append(inner);
        }
        return res.toString();
    }

    private static String[] split(String raw) {
        Matcher matcher = PATTERN.matcher(raw);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result.toArray(new String[result.size()]);
    }

    public static Command from(String name, Object... args) {
        String[] strArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            strArgs[i] = args[i].toString();
        }
        return new Command(name, strArgs);
    }

    public static Command fromRaw(String raw) {
        String[] parts = split(raw);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return new Command(parts[0], args);
    }
}
