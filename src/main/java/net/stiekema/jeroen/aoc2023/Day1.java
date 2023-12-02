package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day1 {

    private static final Map<String, Integer> NUMBER_MAP = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1Test());
        System.out.println("part 1 55538?: " + calculatePart1());
        System.out.println("part 2 test: " + calculatePart2Test());
        System.out.println("part 2 54875?: " + calculatePart2());
    }

    private static int calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day1.class.getResource("/day1-1-test.txt");
        return calculate(resource, Day1::calculateCalibrationValuePart1);
    }

    private static int calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day1.class.getResource("/day1.txt");
        return calculate(resource, Day1::calculateCalibrationValuePart1);
    }

    private static int calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day1.class.getResource("/day1-2-test.txt");
        return calculate(resource, Day1::calculateCalibrationValuePart2);
    }

    private static int calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day1.class.getResource("/day1.txt");
        return calculate(resource, Day1::calculateCalibrationValuePart2);
    }

    private static int calculate(URL resource, Function<String, Integer> calibrationCalculator) throws URISyntaxException, IOException {
        return getLines(resource)
                .map(calibrationCalculator)
                .reduce(Integer::sum)
                .orElseThrow();
    }

    private static int calculateCalibrationValuePart1(String line) {
        Pattern pattern = Pattern.compile("[0-9]");
        LinkedList<String> occurences = getOccurences(pattern, line);
        return asInteger(occurences.peekFirst()) * 10 + asInteger(occurences.peekLast());
    }

    private static int calculateCalibrationValuePart2(String line) {
        Pattern pattern = Pattern.compile(NUMBER_MAP.keySet().stream()
                .reduce("[0-9]", (a, b) -> a + "|" + b));
        LinkedList<String> occurences = getOccurences(pattern, line);
        return asInteger(occurences.peekFirst()) * 10 + asInteger(occurences.peekLast());
    }

    private static LinkedList<String> getOccurences(Pattern pattern, String line) {
        LinkedList<String> results = new LinkedList<>();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            results.add(matcher.group());
            matcher.region(matcher.start() + 1, line.length());
        }
        return results;
    }

    private static int asInteger(String number) {
        if (NUMBER_MAP.keySet().contains(number)) {
            return NUMBER_MAP.get(number);
        } else {
            return Integer.parseInt(number);
        }
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }


}


