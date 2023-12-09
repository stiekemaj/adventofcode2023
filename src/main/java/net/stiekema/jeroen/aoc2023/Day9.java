package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day9 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test: " + calculatePart1("/day9-test.txt"));
        System.out.println("Part 1: " + calculatePart1("/day9.txt"));
        System.out.println("Part 2 test: " + calculatePart2("/day9-test.txt"));
        System.out.println("Part 2: " + calculatePart2("/day9.txt"));
    }

    public static long calculatePart1(String file) throws URISyntaxException, IOException {
        return getLines(file)
                .map(t -> t.split("\\s+"))
                .map(Arrays::asList)
                .map(t -> t.stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()))
                .mapToLong(Day9::extrapolate)
                .sum();
    }

    public static long calculatePart2(String file) throws URISyntaxException, IOException {
        return getLines(file)
                .map(t -> t.split("\\s+"))
                .map(Arrays::asList)
                .map(Utils::reverse)
                .map(t -> t.stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()))
                .mapToLong(Day9::extrapolate)
                .sum();
    }

    private static long extrapolate(List<Long> input) {
        return extrapolate(input, new ArrayDeque<>());
    }

    private static long extrapolate(List<Long> input, Deque<Long> stack) {
        if (input.stream().allMatch(Long.valueOf(0L)::equals)) {
            return stack.stream().reduce(Long::sum).orElse(0L);
        }

        stack.push(input.get(input.size() - 1));
        return extrapolate(
                IntStream.range(1, input.size())
                        .mapToObj(i -> input.get(i) - input.get(i - 1))
                        .toList(),
                stack
        );
    }

    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private static class Utils {
        public static <T> List<T> reverse(List<T> list) {
            ArrayList<T> result = new ArrayList<>(list);
            Collections.reverse(result);
            return result;
        }
    }
}
