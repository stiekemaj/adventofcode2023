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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {

    public static void main(String[] args) throws URISyntaxException, IOException {
//        System.out.println("Part 1 test: " + calculatePart1("/day10-test.txt"));
//        System.out.println("Part 1: " + calculatePart1("/day10.txt"));
        System.out.println("Part 2 test: " + calculatePart2("/day10-test-2.txt"));
        System.out.println("Part 2: " + calculatePart2("/day10.txt"));
    }

    public static long calculatePart1(String file) throws URISyntaxException, IOException {
        Maze.Builder builder = new Maze.Builder();
        getLines(file).forEach(builder::addLine);
        Maze maze = builder.build();
//        System.out.printf(maze.toString());
        List<Node> routeNodes = maze.findRouteNodes();
        return (long) Math.ceil((double) (routeNodes.size() / 2L));
    }

    private static long calculatePart2(String file) throws URISyntaxException, IOException {
        Maze.Builder builder = new Maze.Builder();
        getLines(file).forEach(builder::addLine);
        Maze maze = builder.build();
//        System.out.println(maze);
        EnclosedTilesCalculator calculator = new EnclosedTilesCalculator(maze);
        return calculator.calculateNrOfEnclosedTiles();
    }


    private static Stream<String> getLines(String fileName) throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource(fileName);
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private static class Maze {

        private long width, height;
        private Map<Coordinate, Node> nodes;
        private final Node startingPoint;

        private Maze(long width, long height, Map<Coordinate, Node> nodes, Node startingPoint) {
            this.width = width;
            this.height = height;
            this.nodes = nodes;
            this.startingPoint = startingPoint;
        }

        public Node getStartingPoint() {
            return startingPoint;
        }

        public List<Node> findRouteNodes() {
            List<Node> result = new ArrayList<>();
            Node currentNode = getStartingPoint();
            do {
                result.add(currentNode);
                currentNode = currentNode.next;
            } while (!currentNode.isStartingPoint());
            return result;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(nodes.get(new Coordinate(x, y)).nodeType.ascii);
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
                int width = lines.isEmpty() ? 0 : lines.get(0).length();
                int height = lines.size();
                Map<Coordinate, Node> nodes = new HashMap<>();
                Node startingPoint = null;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        NodeType nodeType = NodeType.of(lines.get(y).charAt(x));
                        Coordinate coordinate = new Coordinate(x, y);
                        Node node = new Node(coordinate, nodeType);
                        nodes.put(coordinate, node);
                        if (node.isStartingPoint()) {
                            startingPoint = node;
                        }
                    }
                }

                Node previous = null;
                Node current = startingPoint;

                do {
                    Node finalPrevious = previous;
                    Node next = current.nodeType.findPossibleNeighbours(current.coordinate)
                            .entrySet()
                            .stream().filter(t -> withinBoundaries(t.getKey(), width, height))
                            .filter(entry -> {
                                Node neighbourNode = nodes.get(entry.getKey());
                                return (neighbourNode != null && entry.getValue().contains(neighbourNode.nodeType));
                            })
                            .map(Map.Entry::getKey)
                            .map(nodes::get)
                            .filter( t -> !t.equals(finalPrevious))
                            .filter(t -> t.previous == null)
                            .findFirst().orElseThrow();
                    current.next = next;
                    next.previous = current;
                    previous = current;
                    current = next;
                } while (!current.isStartingPoint());

                return new Maze(width, height, nodes, startingPoint);
            }

            private static boolean withinBoundaries(Coordinate c, int width, int height) {
                return c.x() >= 0 && c.x() < width && c.y >= 0 && c.y < height;
            }
        }
    }

    private static class EnclosedTilesCalculator {
        private final Maze maze;
        private final Map<Coordinate, Node> routeNodes;
        private final Map<Coordinate, Node> nonRouteNodes;

        private EnclosedTilesCalculator(Maze maze) {
            this.maze = maze;
            List<Node> routeNodes = this.maze.findRouteNodes();
            this.routeNodes = routeNodes.stream().collect(Collectors.toMap(k -> k.coordinate, v -> v));
            this.nonRouteNodes = this.maze.nodes.values().stream()
                    .filter(Predicate.not(routeNodes::contains))
                    .collect(Collectors.toMap(k -> k.coordinate, v -> v));
        }
        
        public long calculateNrOfEnclosedTiles() {
            return this.nonRouteNodes.values()
                    .stream()
                    .filter(this::isEnclosed)
                    .count();

        }

        private boolean isEnclosed(Node node) {
            int crossingLinesLeft = findCrossingLinesLeft(node);
            int crossingLinesRight = findCrossingLinesRight(node);
            int crossingLinesTop = findCrossingLineTop(node);
            int crossingLinesBottom = findCrossingLineBottom(node);

            return crossingLinesLeft % 2 != 0
                    && crossingLinesRight % 2 != 0
                    && crossingLinesTop % 2 != 0
                    && crossingLinesBottom % 2 != 0;
        }

        private int findCrossingLinesLeft(Node node) {
            int result = 0;
            Node previousNode = null;
            for (int x = 0; x < node.coordinate.x; x++) {
                Node foundNode = routeNodes.get(new Coordinate(x, node.coordinate.y));
                if (foundNode != null && !connected(previousNode, foundNode) && crossesY(foundNode)) {
                    result++;
                }
                previousNode = foundNode;
            }
            return result;
        }

        private int findCrossingLinesRight(Node node) {
            int result = 0;
            Node previousNode = null;
            for (int x = node.coordinate.x + 1; x < maze.width; x++) {
                Node foundNode = routeNodes.get(new Coordinate(x, node.coordinate.y));
                if (foundNode != null && !connected(previousNode, foundNode) && crossesY(foundNode)) {
                    result++;
                }
                previousNode = foundNode;
            }
            return result;
        }

        private int findCrossingLineTop(Node node) {
            int result = 0;
            Node previousNode = null;
            for (int y = 0; y < node.coordinate.y; y++) {
                Node foundNode = routeNodes.get(new Coordinate(node.coordinate.x, y));
                if (foundNode != null && !connected(previousNode, foundNode) && crossesX(foundNode)) {
                    result++;
                }
                previousNode = foundNode;
            }
            return result;
        }

        private int findCrossingLineBottom(Node node) {
            int result = 0;
            Node previousNode = null;
            for (int y = node.coordinate.y + 1; y < maze.height; y++) {
                Node foundNode = routeNodes.get(new Coordinate(node.coordinate.x, y));
                if (foundNode != null && !connected(previousNode, foundNode) && crossesX(foundNode)) {
                    result++;
                }
                previousNode = foundNode;
            }
            return result;
        }

        private boolean connected(Node nodeA, Node nodeB) {
            if (nodeA == null || nodeB == null) {
                return false;
            }
            return nodeA.next == nodeB || nodeA.previous == nodeB;
        }

        private boolean crossesY(Node node) {
            int previousY;
            Node currentNode = node;
            do {
                currentNode = currentNode.previous;
                previousY = currentNode.coordinate.y;
            } while (previousY == node.coordinate.y);

            int nextY;
            currentNode = node;
            do {
                currentNode = currentNode.next;
                nextY = currentNode.coordinate.y;
            } while (nextY == node.coordinate.y);

            return previousY != nextY;
        }

        private boolean crossesX(Node node) {
            int previousX;
            Node currentNode = node;
            do {
                currentNode = currentNode.previous;
                previousX = currentNode.coordinate.x;
            } while (previousX == node.coordinate.x);

            int nextX;
            currentNode = node;
            do {
                currentNode = currentNode.next;
                nextX = currentNode.coordinate.x;
            } while (nextX == node.coordinate.x);

            return previousX != nextX;
        }
    }

    private static class Node {
        private final Coordinate coordinate;
        private final NodeType nodeType;
        private Node next;
        private Node previous;

        public Node(Coordinate coordinate, NodeType nodeType) {
            this.coordinate = coordinate;
            this.nodeType = nodeType;
        }

        public boolean isStartingPoint() {
            return this.nodeType == NodeType.START;
        }

        public void connect(Node node) {
            if (this.next == null) {
                this.next = node;
                node.previous = this;
            } else if (this.previous == null) {
                this.previous = node;
                node.next = this;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private enum NodeType {
        NS('|', '│'),
        EW('-', '─'),
        NE('L', '└'),
        NW('J', '┘'),
        SW('7', '┐'),
        SE('F', '┌'),
        GROUND('.', '.'),
        START('S', '┼');

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

    private record Coordinate(int x, int y) {}
}
