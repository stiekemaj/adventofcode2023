package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Day10 {

    public static void main(String[] args) throws URISyntaxException, IOException {
//        System.out.println("Part 1 test: " + calculatePart1("/day10-test.txt"));
//        System.out.println("Part 1: " + calculatePart1("/day10.txt"));
        System.out.println("Part 2 test: " + calculatePart2("/day10-test-2.txt"));
        System.out.println("Part 2: " + calculatePart2("/day10.txt"));
    }

    private static long calculatePart2(String file) throws URISyntaxException, IOException {
        Maze.Builder builder = new Maze.Builder();
        getLines(file).forEach(builder::addLine);
        Maze maze = builder.build();
        System.out.println(maze.toString());
        return 0L;
    }

    public static long calculatePart1(String file) throws URISyntaxException, IOException {
        Maze.Builder builder = new Maze.Builder();
        getLines(file).forEach(builder::addLine);
        Maze maze = builder.build();
        System.out.printf(maze.toString());
        Context context = new Context();
        Node currentNode = maze.getStartingPoint();
        do {
            currentNode = currentNode.next(context);
        } while (!currentNode.isStartingPoint());
        return context.stepsToFarthestPosition();
    }


    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private static class Maze {

        private final Node[][] representation;
        private final Node startingPoint;

        private Maze(Node[][] representation, Node startingPoint) {
            this.representation = representation;
            this.startingPoint = startingPoint;
        }

        public Node getStartingPoint() {
            return startingPoint;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < representation.length; y++) {
                for (int x = 0; x < representation[0].length; x++) {
                    sb.append(representation[y][x].nodeType.ascii);
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        private static class Builder {
            private final List<String> lines = new ArrayList<>();

            private void addLine(String line) {
                lines.add(line);
            }

            private Maze build() {
                int lineLength = lines.isEmpty() ? 0 : lines.get(0).length();
                Node[][] representation = new Node[lines.size()][lineLength];
                Node startingPoint = null;
                for (int y = 0; y < lines.size(); y++) {
                    for (int x = 0; x < lineLength; x++) {
                        NodeType nodeType = NodeType.of(lines.get(y).charAt(x));
                        Node node = new Node(new Coordinate(x, y), nodeType);
                        representation[y][x] = node;
                        if (node.isStartingPoint()) {
                            startingPoint = node;
                        }
                        nodeType.findPossibleNeighbours(new Coordinate(x, y))
                                .entrySet()
                                .stream()
                                .filter(t -> withinBoundaries(t.getKey(), representation))
                                .forEach(t -> {
                                    Node neighbourNode = representation[t.getKey().y][t.getKey().x];
                                    if (neighbourNode != null && t.getValue().contains(neighbourNode.nodeType)) {
                                        node.connect(neighbourNode);
                                    }
                                });

                    }
                }


                return new Maze(representation, startingPoint);
            }

            private static boolean withinBoundaries(Coordinate c, Node[][] representation) {
                return c.x() >= 0 && c.x() < representation[0].length && c.y >= 0 && c.y < representation.length;
            }
        }
    }

    private static class Node {
        private final Set<Node> linkedNodes = new HashSet<>();
        private final Coordinate coordinate;
        private final NodeType nodeType;

        public Node(Coordinate coordinate, NodeType nodeType) {
            this.coordinate = coordinate;
            this.nodeType = nodeType;
        }

        public Node next(Context context) {
            context.nrOfSteps++;
            Node next = linkedNodes.stream()
                    .filter(t -> !t.equals(context.previous))
                    .findFirst()
                    .orElseThrow();
            context.previous = this;
            return next;
        }

        public boolean isStartingPoint() {
            return this.nodeType == NodeType.START;
        }

        public void connect(Node node) {
            this.linkedNodes.add(node);
            node.linkedNodes.add(this);
        }
    }

    private enum NodeType {
        NS('|', '│'), EW('-', '─'), NE('L', '└'), NW('J', '┘'), SW('7', '┐'), SE('F', '┌'), GROUND('.', '.'), START('S', '┼');

        private final char c;
        private final char ascii;

        NodeType(char c, char ascii) {
            this.c = c;
            this.ascii = ascii;
        }

        private static NodeType of(char c) {
            return Arrays.stream(NodeType.values())
                    .filter(e -> e.c == c)
                    .findFirst()
                    .orElseThrow();
        }

        private Map<Coordinate, List<NodeType>> findPossibleNeighbours(Coordinate c) {
            return switch (this) {
                case NE -> Map.of(
                        coordNorth(c), compatibleTypesNorth(),
                        coordEast(c), compatibleTypesEast());
                case NW -> Map.of(
                        coordNorth(c), compatibleTypesNorth(),
                        coordWest(c), compatibleTypesWest());
                case NS -> Map.of(
                        coordNorth(c), compatibleTypesNorth(),
                        coordSouth(c), compatibleTypesSouth());
                case EW -> Map.of(
                        coordEast(c), compatibleTypesEast(),
                        coordWest(c), compatibleTypesWest());
                case SW -> Map.of(
                        coordSouth(c), compatibleTypesSouth(),
                        coordWest(c), compatibleTypesWest());
                case SE -> Map.of(
                        coordSouth(c), compatibleTypesSouth(),
                        coordEast(c), compatibleTypesEast());
                case START -> Map.of(
                        coordNorth(c), compatibleTypesNorth(),
                        coordSouth(c), compatibleTypesSouth(),
                        coordEast(c), compatibleTypesEast(),
                        coordWest(c), compatibleTypesWest());
                default -> Collections.emptyMap();
            };
        }

        private static Coordinate coordNorth(Coordinate c) {
            return new Coordinate(c.x, c.y - 1);
        }

        private static Coordinate coordSouth(Coordinate c) {
            return new Coordinate(c.x, c.y + 1);
        }

        private static Coordinate coordEast(Coordinate c) {
            return new Coordinate(c.x + 1, c.y);
        }

        private static Coordinate coordWest(Coordinate c) {
            return new Coordinate(c.x - 1, c.y);
        }

        private static List<NodeType> compatibleTypesNorth() {
            return List.of(NS, SW, SE, START);
        }

        private static List<NodeType> compatibleTypesSouth() {
            return List.of(NE, NW, NS, START);
        }

        private static List<NodeType> compatibleTypesEast() {
            return List.of(EW, NW, SW, START);
        }

        private static List<NodeType> compatibleTypesWest() {
            return List.of(EW, NE, SE, START);
        }
    }
    private static class Context {

        private Node previous;
        private long nrOfSteps = 0;

        public long stepsToFarthestPosition() {
            return (long) Math.ceil(nrOfSteps / 2L);
        }
    }

    private record Coordinate(int x, int y) {}
}
