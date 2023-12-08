package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day8 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test 1: " + calculatePart1("/day8-test.txt"));
        System.out.println("Part 1 test 2: " + calculatePart1("/day8-test2.txt"));
        System.out.println("Part 1: " + calculatePart1("/day8.txt"));
        System.out.println("Part 2 test: " + calculatePart2("/day8-test3.txt"));
        System.out.println("Part 2: " + calculatePart2("/day8.txt"));
    }

    private static long calculatePart1(String file) throws URISyntaxException, IOException {
        long startTime = System.currentTimeMillis();
        Struct struct = parseFile(file);
        long result = calculate(List.of("AAA"), struct, t -> t.equals("ZZZ"));
        System.out.println("time spent: " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

    private static long calculatePart2(String file) throws URISyntaxException, IOException {
        long startTime = System.currentTimeMillis();
        Struct struct = parseFile(file);
        List<String> startingPoints = struct.network.keySet().stream()
                .filter(t -> t.endsWith("A"))
                .toList();
        long result = calculate(startingPoints, struct, t -> t.endsWith("Z"));
        System.out.println("time spent: " + (System.currentTimeMillis() - startTime) + " ms");
        return result;
    }

    private static long calculate(List<String> startingPoints, Struct struct, Predicate<String> finalNodeMatcher) {
        return startingPoints.stream()
                .map(t -> calculate(t, struct, finalNodeMatcher))
                .reduce(Day8::lcm).orElseThrow();
    }

    private static long calculate(String startingPoint, Struct struct, Predicate<String> finalNodeMatcher) {
        long stepNr = 0;
        Tuple<String> currentNode = struct.network.get(startingPoint);
        while(true) {
            Instruction nextInstruction = struct.instructions.get((int)(stepNr++ % struct.instructions.size()));
            String nextStep = nextInstruction.nextFrom(currentNode);
            if (finalNodeMatcher.test(nextStep)) {
                return stepNr;
            }
            currentNode = struct.network.get(nextStep);
        }
    }

    private static long lcm(long a, long b) {
        return (a * b) / gcd(a, b);
    }

    private static long gcd(long a, long b) {
        while (a != b) {
            if (a > b) {
                a = a - b;
            } else {
                b = b - a;
            }
        }
        return a;
    }

    private static Struct parseFile(String fileName) throws URISyntaxException, IOException {
        List<Instruction> instructions = getLines(fileName).findFirst()
                .map(t -> t.chars()
                        .mapToObj(c -> (char) c)
                        .map(Instruction::of)
                        .toList()
                ).orElseThrow();

        Map<String, Tuple<String>> network = new HashMap<>();
        getLines(fileName)
                .skip(2)
                .map(t -> t.split("[\\s=(),]+"))
                .forEach(t -> network.put(t[0], new Tuple<>(t[1], t[2])));

        return new Struct(instructions, network);
    }

    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private enum Instruction {
        LEFT, RIGHT;

        private static Instruction of(char c) {
            if (c == 'L') return LEFT;
            if (c == 'R') return RIGHT;
            throw new IllegalArgumentException("" + c);
        }

        public String nextFrom(Tuple<String> tuple) {
            if (this == LEFT) return tuple.left();
            else return tuple.right();
        }
    }

    private record Struct(List<Instruction> instructions, Map<String, Tuple<String>> network) {}

    private record Tuple<T>(T left, T right) {}
}
