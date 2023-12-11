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
import java.util.stream.Stream;

public class Day11 {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculate("/day11-test.txt", 2));
        System.out.println("part 1: " + calculate("/day11.txt", 2));
        System.out.println("part 2 test: " + calculate("/day11-test.txt", 10));
        System.out.println("part 2: " + calculate("/day11.txt", 1_000_000));
    }

    private static long calculate(String file, int replaceFactor) throws URISyntaxException, IOException {
        List<String> lines = getLines(file).toList();
        Character[][] image = new Character[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(0).length(); x++) {
                image[y][x] = lines.get(y).charAt(x);
            }
        }
        List<Coord> galaxyCoordinates = getGalaxyCoordinates(image, replaceFactor);
        return calculateDistanceOfPairs(galaxyCoordinates);
    }

    private static long calculateDistanceOfPairs(List<Coord> galaxyCoordinates) {
        long total = 0;
        for (int i = 0; i < galaxyCoordinates.size() - 1; i++) {
            for (int j = i + 1; j < galaxyCoordinates.size(); j++) {
                long distance = calculateDistance(galaxyCoordinates.get(i), galaxyCoordinates.get(j));
                total += distance;
            }
        }
        return total;
    }

    private static long calculateDistance(Coord a, Coord b) {
        return Math.max(a.x, b.x) - Math.min(a.x, b.x)
                + Math.max(a.y, b.y) - Math.min(a.y, b.y);
    }

    private static List<Coord> getGalaxyCoordinates(Character[][] image, int replaceFactor) {
        List<Coord> result = new ArrayList<>();
        List<Integer> emptyXPositions = findEmptyXPositions(image);
        List<Integer> emptyYPositions = findEmptyYPositions(image);
        long expandedY = 0;
        long expandedX = 0;

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                if (image[y][x] == '#') {
                    result.add(new Coord(expandedX, expandedY));
                }
                if (emptyXPositions.contains(x)) {
                    expandedX += replaceFactor;
                } else {
                    expandedX++;
                }
            }
            expandedX = 0;

            if (emptyYPositions.contains(y)) {
                expandedY += replaceFactor;
            } else {
                expandedY++;
            }
        }
        return result;
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

    private static List<Integer> findEmptyYPositions(Character[][] image) {
        List<Integer> result = new ArrayList<>();
        for (int y = 0; y < image.length; y++) {
            if (Arrays.stream(image[y]).noneMatch(c -> c == '#')) result.add(y);
        }
        return result;
    }

    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }


    private record Coord(long x, long y) {}
}
