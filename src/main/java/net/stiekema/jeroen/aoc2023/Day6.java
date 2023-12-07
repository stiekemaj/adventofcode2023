package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day6 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test: " + calculatePart1Test());
        System.out.println("Part 1: " + calculatePart1());
        System.out.println("Part 2 test: " + calculatePart2Test());
        System.out.println("Part 2: " + calculatePart2());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day6-test.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day6.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day6-test.txt");
        return calculatePart2(resource);
    }

    private static long calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day6.txt");
        return calculatePart2(resource);
    }

    private static long calculatePart1(URL resource) throws URISyntaxException, IOException {
        List<Record> records = parseToRecordList(resource);
        return calculate(records);
    }

    private static long calculatePart2(URL resource) throws URISyntaxException, IOException {
        List<Record> records = parseToRecordListPart2(resource);
        return calculate(records);
    }

    private static long calculate(List<Record> records) {
        return records.stream()
                .map(Day6::calculateIntersectionsNewRecord)
                .map(t -> (long)Math.floor(t.x2) - (long)Math.ceil(t.x1) + 1)
                .reduce(Math::multiplyExact)
                .orElse(0L);
    }

    private static List<Record> parseToRecordList(URL resource) throws URISyntaxException, IOException {
        List<String> lines = getLines(resource).toList();
        String[] timeArray = lines.get(0).split("\\s+");
        String[] distanceArray = lines.get(1).split("\\s+");
        return IntStream.range(1, timeArray.length)
                .mapToObj(i -> new Record(Long.parseLong(timeArray[i].trim()), Long.parseLong(distanceArray[i].trim())))
                .toList();
    }

    private static List<Record> parseToRecordListPart2(URL resource) throws URISyntaxException, IOException {
        List<String> lines = getLines(resource).toList();
        String time = lines.get(0).replaceAll("\\s+", "").split(":")[1];
        String distance = lines.get(1).replaceAll("\\s+", "").split(":")[1];
        return List.of(new Record(Long.parseLong(time), Long.parseLong(distance)));
    }

    public static IntersectionResult calculateIntersectionsNewRecord(Record record) {
        return quadraticFormula(1L, -record.time, record.distance + 1);
    }

    private static IntersectionResult quadraticFormula(long a, long b, long c) {
        double discriminant = Math.pow(b, 2) - (4 * a * c);
        double x1 = (-b - Math.sqrt(discriminant)) / 2 * a;
        double x2 = (-b + Math.sqrt(discriminant)) / 2 * a;
        return new IntersectionResult(x1, x2);
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    public record Record(long time, long distance) {}

    public record IntersectionResult(double x1, double x2) {}
}
