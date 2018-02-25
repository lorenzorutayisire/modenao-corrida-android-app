package org.upperlevel.corrida.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {
    private static Pattern PATTERN = Pattern.compile("([^\\s\"]+)|(?:\"(.+?[^\\\\])\")");

    public abstract String encode();

    public static String[] split(String raw) {
        Matcher matcher = PATTERN.matcher(raw);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result.toArray(new String[result.size()]);
    }

    public static int toInt(String arg) {
        int result;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse '" + arg + "' to 'int'");
        }
        return result;
    }
    
    public static float toFloat(String arg) {
        float result;
        try {
            result = Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse '" + arg + "' to 'float'");
        }
        return result;
    }

    public abstract Command decode(String[] split);
}
