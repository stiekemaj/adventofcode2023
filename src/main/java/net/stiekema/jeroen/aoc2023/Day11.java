package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day11 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1("/day11-test.txt"));
        System.out.println("part 1: " + calculatePart1("/day11.txt"));
    }

    private static long calculatePart1(String file) throws URISyntaxException, IOException {
        List<String> lines = getLines(file)
                .toList();
        Character[][] image = new Character[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(0).length(); x++) {
                image[y][x] = lines.get(y).charAt(x);
            }
        }
        image = expand(image);
        List<Coord> galaxyCoordinates = getGalaxyCoordinates(image);
        return calculateDistanceOfPairs(galaxyCoordinates);
    }

    private static long calculateDistanceOfPairs(List<Coord> galaxyCoordinates) {
        long total = 0;
        for (int i = 0; i < galaxyCoordinates.size() - 1; i++) {
            for (int j = i + 1; j < galaxyCoordinates.size(); j++) {
                long distance = calculateDistance(galaxyCoordinates.get(i), galaxyCoordinates.get(j));
                System.out.println("a: " + galaxyCoordinates.get(i) + ", b: " + galaxyCoordinates.get(i+1) + " result: " + distance);
                total += distance;
            }
        }
        return total;
    }

    private static long calculateDistance(Coord a, Coord b) {
        return Math.max(a.x, b.x) - Math.min(a.x, b.x)
                + Math.max(a.y, b.y) - Math.min(a.y, b.y);
    }

    private static List<Coord> getGalaxyCoordinates(Character[][] image) {
        List<Coord> result = new ArrayList<>();
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                if (image[y][x] == '#') {
                    result.add(new Coord(x, y));
                }
            }
        }
        return result;
    }

    private static Character[][] expand(Character[][] image) {
        List<Integer> emptyXPositions = findEmptyXPositions(image);
        List<Character[]> lines = new ArrayList<>();
        for (int y = 0; y < image.length; y++) {
            Character[] inputLine = image[y];
            if (Arrays.stream(inputLine).noneMatch(character -> character == '#')) {
                Character[] newLine = new Character[inputLine.length + emptyXPositions.size()];
                Arrays.fill(newLine, '.');
                lines.add(newLine);
            }
            List<Character> newLine = new ArrayList<>();
            for (int x = 0; x < image[0].length; x++) {
                newLine.add(image[y][x]);
                if (emptyXPositions.contains(x)) {
                    newLine.add(image[y][x]);
                }
            }
            lines.add(newLine.toArray(new Character[0]));
        }

        return lines.toArray(new Character[0][]);
    }

    private static List<Integer> findEmptyXPositions(Character[][] image) {
        List<Integer> result = new ArrayList<>();
        for (int x = 0; x < image[0].length; x++) {
            boolean galaxyFound = false;
            for (Character[] chars : image) {
                if (chars[x] == '#') {
                    galaxyFound = true;
                    break;
                }
            }
            if (!galaxyFound) result.add(x);
        }
        return result;
    }

    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }



    private record Coord(int x, int y) {}
}
