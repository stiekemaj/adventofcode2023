package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day3 {
    private static final boolean DEBUG = false;

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1Test());
        System.out.println("part 1 537832?: " + calculatePart1());
        System.out.println("part 2 test: " + calculatePart2Test());
        System.out.println("part 2: " + calculatePart2());
    }

    private static int calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day3-1-test.txt");
        return calculate(resource);
    }

    private static int calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day3.txt");
        return calculate(resource);
    }

    private static int calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day3-1-test.txt");
        return calculateGearRatio(resource);
    }

    private static int calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day2.class.getResource("/day3.txt");
        return calculateGearRatio(resource);
    }

    private static int calculate(URL resource) throws URISyntaxException, IOException {
        Engine engine = buildEngine(resource);
        List<Integer> enginePartNrs = engine.findEnginePartNrs();
        if (DEBUG) System.out.println(enginePartNrs);
        return enginePartNrs.stream().reduce(Integer::sum).orElse(0);
    }

    private static int calculateGearRatio(URL resource) throws URISyntaxException, IOException {
        Engine engine = buildEngine(resource);
        List<Integer> gearRatios = engine.findGearRatios();
        if (DEBUG) System.out.println(gearRatios);
        return gearRatios.stream().reduce(Integer::sum).orElse(0);
    }

    private static Engine buildEngine(URL resource) throws URISyntaxException, IOException {
        Engine.Builder engineBuilder = new Engine.Builder();
        getLines(resource).forEach(engineBuilder::addLine);
        return engineBuilder.build();
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    public static class Engine {

        private static final List<Character> SYMBOLS = IntStream.range(0, 255)
                .mapToObj(t -> (char) t)
                .filter(t -> t != '.')
                .filter(t -> t < '0' || t > '9')
                .toList();

        // representation[0] = first line, representation[0][5] = first line, 6th character
        private final char[][] representation;

        private Engine(char[][] representation) {
            this.representation = representation;
        }

        public List<Integer> findEnginePartNrs() {
            List<Integer> result = new ArrayList<>();
            for (int lineNr = 0; lineNr < representation.length; lineNr++) {
                String line = new String(representation[lineNr]);
                result.addAll(findEnginePartNrs(line, lineNr));
            }
            return result;
        }

        public List<Integer> findGearRatios() {
            Map<Coordinate, Gear> possibleGears = new HashMap<>();
            for (int y = 0; y < representation.length; y++) {
                String line = new String(representation[y]);
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String match = matcher.group();
                    int startIndex = matcher.start();
                    int endIndex = matcher.end() - 1;
                    Set<Coordinate> foundGears = findAdjacentToSymbolCoordinates(new Coordinate(startIndex, y), new Coordinate(endIndex, y), List.of('*'));
                    foundGears.forEach(t -> {
                                Gear gear = possibleGears.getOrDefault(t, new Gear(t));
                                gear.addPartNr(Integer.parseInt(match));
                                possibleGears.put(t, gear);
                            });
                }
            }

            return possibleGears.values().stream()
                    .filter(Gear::isValidGear)
                    .map(Gear::getGearRatio)
                    .toList();
        }

        private List<Integer> findEnginePartNrs(String line, int lineNr) {
            List<Integer> result = new ArrayList<>();
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String match = matcher.group();
                int startIndex = matcher.start();
                int endIndex = matcher.end() - 1;
                if (!findAdjacentToSymbolCoordinates(new Coordinate(startIndex, lineNr), new Coordinate(endIndex, lineNr), SYMBOLS).isEmpty()) {
                    result.add(Integer.parseInt(match));
                }
            }
            return result;
        }

        private Set<Coordinate> findAdjacentToSymbolCoordinates(Coordinate start, Coordinate end, List<Character> symbols) {
            Set<Coordinate> result = new HashSet<>();
            for (int x = start.x; x <= end.x; x++) {
                result.addAll(findAdjacentToSymbolCoordinates(new Coordinate(x, start.y), symbols));
            }
            return result;
        }

        private Set<Coordinate> findAdjacentToSymbolCoordinates(Coordinate coordinate, List<Character> symbols) {
            Set<Coordinate> result = new HashSet<>();
            for (int x = coordinate.x - 1; x <= coordinate.x + 1; x++) {
                for (int y = coordinate.y - 1; y <= coordinate.y + 1; y++) {
                    char character = representation[y][x];
                    if (symbols.contains(character)) {
                        result.add(new Coordinate(x, y));
                    }
                }
            }
            return result;
        }

        private static class Builder {
            private final LinkedList<String> lines = new LinkedList<>();

            private void addLine(String line) {
                lines.add(line);
            }

            private Engine build() {
                int lineLength = lines.isEmpty() ? 0 : lines.peek().length();
                char[][] representation = new char[lineLength + 2][lines.size() + 2];

                int y = 0;
                Arrays.fill(representation[y++], '.');
                for (String line : lines) {
                    int x = 0;
                    representation[y][x++] = '.';
                    for (char c : line.toCharArray()) {
                        representation[y][x++] = c;
                    }
                    representation[y][x] = '.';
                    y++;
                }
                Arrays.fill(representation[y], '.');
                if (DEBUG) Arrays.stream(representation).forEach(System.out::println);
                return new Engine(representation);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (char[] line : representation) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public record Coordinate (int x, int y) {
    }

    public static class Gear {
        private final Coordinate coordinate;
        private final List<Integer> partNumbers = new ArrayList<>();

        public Gear(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        public void addPartNr(Integer partNr) {
            this.partNumbers.add(partNr);
        }

        public boolean isValidGear() {
            return partNumbers.size() == 2;
        }

        public int getGearRatio() {
            if (partNumbers.size() != 2) {
                throw new IllegalStateException("not a valid gear");
            }

            return partNumbers.get(0) * partNumbers.get(1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Gear gear = (Gear) o;
            return Objects.equals(coordinate, gear.coordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate);
        }
    }


}
