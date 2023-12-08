package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Struct struct = buildStruct(file);
        return struct.calculateSteps(t -> t.equals("AAA"), t -> t.equals("ZZZ"));
    }

    private static long calculatePart2(String file) throws URISyntaxException, IOException {
        Struct struct = buildStruct(file);
        return struct.calculateSteps(t -> t.endsWith("A"), t1 -> t1.endsWith("Z"));
    }

    private static Struct buildStruct(String fileName) throws URISyntaxException, IOException {
        List<Character> instructions = getLines(fileName).findFirst()
                .map(t -> t.chars()
                        .mapToObj(c -> (char) c)
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


    private record Struct(List<Character> instructions, Map<String, Tuple<String>> network) {
        private long calculateSteps(Predicate<String> startPredicate, Predicate<String> finishPredicate) {
            return network.keySet().stream()
                    .filter(startPredicate)
                    .map(t -> calculateSteps(t, finishPredicate))
                    .reduce(Math::lcm).orElseThrow();
        }

        private long calculateSteps(String startingPoint, Predicate<String> finishPredicate) {
            long stepNr = 0;
            Tuple<String> currentNode = network.get(startingPoint);
            while(true) {
                Character nextInstruction = instructions.get((int)(stepNr++ % instructions.size()));
                String nextStep = nextInstruction == 'L' ? currentNode.left : currentNode.right;
                if (finishPredicate.test(nextStep)) {
                    return stepNr;
                }
                currentNode = network.get(nextStep);
            }
        }
    }

    private record Tuple<T>(T left, T right) {}

    private static final class Math {
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
    }
}
