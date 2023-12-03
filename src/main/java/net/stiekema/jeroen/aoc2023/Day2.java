package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1Test());
        System.out.println("part 1 1931?: " + calculatePart1());
        System.out.println("part 2 test: " + calculatePart2Test());
        System.out.println("part 2 83105?: " + calculatePart2());
    }

    private static int calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day2-1-test.txt");
        CubeHolder bag = new CubeHolder(12, 13, 14);
        return calculatePossibleGames(resource, bag);
    }

    private static int calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day2.txt");
        CubeHolder bag = new CubeHolder(12, 13, 14);
        return calculatePossibleGames(resource, bag);
    }

    private static int calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day2-1-test.txt");
        return calculateMinimumCubes(resource);
    }

    private static int calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day2.txt");
        return calculateMinimumCubes(resource);
    }

    private static int calculatePossibleGames(URL resource, CubeHolder bag) throws URISyntaxException, IOException {
        return getLines(resource)
                .map(Day2::convertToGame)
                .filter(t -> t.isPossibleWithBag(bag))
                .map(Game::getId)
                .reduce(Integer::sum)
                .orElseThrow();
    }

    private static int calculateMinimumCubes(URL resource) throws URISyntaxException, IOException {
        return getLines(resource)
                .map(Day2::convertToGame)
                .map(Game::getMinimumBag)
                .map(CubeHolder::getPower)
                .reduce(Integer::sum)
                .orElseThrow();
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private static Game convertToGame(String line) {
        int id = getGameId(line);
        String parts = line.substring(line.indexOf(":") + 1).trim();
        List<CubeHolder> cubeSets = Arrays.stream(parts.split(";"))
                .map(String::trim)
                .map(CubeHolder::asCubeHolder)
                .toList();
        return new Game(id, cubeSets);
    }

    private static int getGameId(String line) {
        Pattern pattern = Pattern.compile("Game ([0-9]+).*?");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String result = matcher.group(1);
            return Integer.parseInt(result);
        } else {
            throw new IllegalStateException("no id found for line '" + line + "'");
        }
    }


    private static class Game {
        private final int id;
        private final List<CubeHolder> cubeSets;

        public Game(int id, List<CubeHolder> cubeSets) {
            this.id = id;
            this.cubeSets = cubeSets;
        }

        public int getId() {
            return id;
        }

        public boolean isPossibleWithBag(CubeHolder bag) {
            return cubeSets.stream()
                    .allMatch(t -> t.isSubsetOf(bag));
        }

        public CubeHolder getMinimumBag() {
            int redCubes = cubeSets.stream()
                    .map(CubeHolder::getRedCubes)
                    .max(Integer::compareTo)
                    .orElse(0);
            int greenCubes = cubeSets.stream()
                    .map(CubeHolder::getGreenCubes)
                    .max(Integer::compareTo)
                    .orElse(0);
            int blueCubes = cubeSets.stream()
                    .map(CubeHolder::getBlueCubes)
                    .max(Integer::compareTo)
                    .orElse(0);

            return new CubeHolder(redCubes, greenCubes, blueCubes);
        }
    }

    private static class CubeHolder {
        private final int redCubes;
        private final int greenCubes;
        private final int blueCubes;

        public CubeHolder(int redCubes, int greenCubes, int blueCubes) {
            this.redCubes = redCubes;
            this.greenCubes = greenCubes;
            this.blueCubes = blueCubes;
        }

        public int getRedCubes() {
            return redCubes;
        }

        public int getGreenCubes() {
            return greenCubes;
        }

        public int getBlueCubes() {
            return blueCubes;
        }

        public static CubeHolder asCubeHolder(String representation) {
            int red = 0;
            int green = 0;
            int blue = 0;
            String[] colors = representation.split(",");
            for (String color : colors) {
                String trimmedColor = color.trim();
                int count = Integer.parseInt(trimmedColor.substring(0, trimmedColor.indexOf(" ")));
                if (trimmedColor.endsWith("red")) {
                    red = count;
                } else if (trimmedColor.endsWith("green")) {
                    green = count;
                } else if (trimmedColor.endsWith("blue")) {
                    blue = count;
                }
            }
            return new CubeHolder(red, green, blue);
        }

        public boolean isSubsetOf(CubeHolder cubeHolder) {
            return this.redCubes <= cubeHolder.getRedCubes()
                    && this.greenCubes <= cubeHolder.getGreenCubes()
                    && this.blueCubes <= cubeHolder.getBlueCubes();
        }

        public int getPower() {
            return redCubes * greenCubes * blueCubes;
        }
    }
}
