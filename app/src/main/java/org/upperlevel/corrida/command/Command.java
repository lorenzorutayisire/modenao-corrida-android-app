package org.upperlevel.corrida.command;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {
   // ('([^\s"]+)|(?:"(.+?[^\\\\])")')
    public static final Pattern SPLITTER = Pattern.compile("([^\\s\"]+)|(?:\"(.+?[^\\\\])\")");
    public static final String ENCODING = "UTF-8";

    private String name;
    private String[] arguments;
    private String string, stringWithEnd;

    private Command() {
    }

    public String getName() {
        return name;
    }

    public String getArgument(int index) {
        return arguments[index];
    }

    public String[] getArguments() {
        return arguments;
    }

    public String toString() {
        return string;
    }

    public byte[] format() {
        try {
            return stringWithEnd.getBytes(ENCODING);
        } catch (UnsupportedEncodingException ignored) {
            throw new IllegalStateException("Cannot convert string to bytes");
        }
    }

    private static String[] splitCommand(String raw) {
        Matcher m = SPLITTER.matcher(raw);
        List<String> r = new ArrayList<>();
        while (m.find()) {
            r.add(m.group());
        }
        return r.toArray(new String[r.size()]);
    }

    public static Command parse(String string) {
        Command cmd = new Command();
        cmd.string = string;
        cmd.stringWithEnd = string.endsWith("\n") ? string : string + "\n"; // puts \n at the end fo string
        String[] pieces = splitCommand(string);
        System.out.println(string + " -> " + Arrays.toString(pieces));
        cmd.name = pieces[0];
        if (pieces.length > 1) {
            cmd.arguments = new String[pieces.length - 1];
            System.arraycopy(pieces, 1, cmd.arguments, 0, cmd.arguments.length - 1);
        } else {
            cmd.arguments = new String[0];
        }
        return cmd;
    }
}
