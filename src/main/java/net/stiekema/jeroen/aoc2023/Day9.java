package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day9 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test: " + calculatePart1("/day9-test.txt"));
        System.out.println("Part 1: " + calculatePart1("/day9.txt"));
    }

    public static long calculatePart1(String file) throws URISyntaxException, IOException {
        return getLines(file)
                .map(t -> t.split("\\s+"))
                .map(t -> Arrays.asList(t).stream()
                        .map(Long::valueOf)
                        .toList()
                )
                .mapToLong(Day9::extrapolateNextValue)
                .sum();
    }

    private static long extrapolateNextValue(List<Long> values) {
        List<Long> previousDifferences = new LinkedList<>(values);
        List<Long> finalValues = new ArrayList<>();
        while (!previousDifferences.isEmpty() && !previousDifferences.stream().allMatch(Long.valueOf(0L)::equals)) {
            finalValues.add(previousDifferences.get(previousDifferences.size() - 1));
            previousDifferences = calculateDifferences(previousDifferences);
        }

        return finalValues.stream()
                .mapToLong(t -> t)
                .sum();
    }

    private static List<Long> calculateDifferences(List<Long> values) {
        if (values.size() < 2) {
            return values;
        }

        return IntStream.range(1, values.size())
                .mapToObj(i -> values.get(i) - values.get(i-1))
                .collect(Collectors.toList());
    }

    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

}
