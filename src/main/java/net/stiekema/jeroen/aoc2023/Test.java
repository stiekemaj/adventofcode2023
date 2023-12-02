package net.stiekema.jeroen.aoc2023;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String input = "oneight";
        String pattern = "(one|two|three|four|five|six|seven|eight|nine|ten)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        while (matcher.find()) {
            String match = matcher.group();
            System.out.println("Match: " + match);
            matcher.region(matcher.start() + 1, input.length());
        }
    }
}
