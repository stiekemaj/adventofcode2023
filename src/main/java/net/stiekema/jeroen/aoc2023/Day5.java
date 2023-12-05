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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
    public static void main(String[] args) throws URISyntaxException, IOException {
//        System.out.println("part 1 test: " + calculatePart1Test());
//        System.out.println("part 1: " + calculatePart1());
//        System.out.println("part 2 test: " + calculatePart2Test());
        System.out.println("part 2: " + calculatePart2());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5-test.txt");
        return calculate(resource, seedResolverPart1());
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5.txt");
        return calculate(resource, seedResolverPart1());
    }

    private static long calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5-test.txt");
        return calculate(resource, seedResolverPart2());
    }

    private static long calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5.txt");
        return calculate(resource, seedResolverPart2());
    }

    private static long calculate(URL resource, Function<String, List<Long>> seedResolver) throws URISyntaxException, IOException {
        return calculateLowestLocationNr(resource, seedResolver);
    }

    private static Long calculateLowestLocationNr(URL resource, Function<String, List<Long>> seedResolver) throws URISyntaxException, IOException {
        Almanac almanac = parseAlmanac(resource, seedResolver);

        return almanac.seeds.stream()
                .map(t -> almanac.seedToSoilMap.getDestination(t))
                .map(t -> almanac.soilToFertilizerMap.getDestination(t))
                .map(t -> almanac.fertilizerToWaterMap.getDestination(t))
                .map(t -> almanac.waterToLightMap.getDestination(t))
                .map(t -> almanac.lightToTemperatureMap.getDestination(t))
                .map(t -> almanac.temperatureToHumidityMap.getDestination(t))
                .map(t -> almanac.humidityToLocationMap.getDestination(t))
                .min(Long::compare)
                .orElseThrow();
    }

    private static Function<String, List<Long>> seedResolverPart1() {
        return line -> Arrays.stream(line.substring(line.indexOf(":") + 1).trim().split(" "))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private static Function<String, List<Long>> seedResolverPart2() {
        return line -> {
            List<Long> result = new ArrayList<>();
            String[] seedValues = line.substring(line.indexOf(":") + 1).trim().split(" ");
            for (int i = 1; i < seedValues.length; i = i + 2) {
                long start = Long.parseLong(seedValues[i - 1]);
                long length = Long.parseLong(seedValues[i]);
                for (long j = start; j < start + length; j++) {
                    result.add(j);
                }
            }
            return result;
        };
    }

    private static Almanac parseAlmanac(URL resource, Function<String, List<Long>> seedResolver) throws URISyntaxException, IOException {
        Almanac almanac = new Almanac();
        AtomicReference<AlmanacMap> almanacMap = new AtomicReference<>();
        getLines(resource)
                .forEach(line -> {
                    if (line.startsWith("seeds:")) {
                        almanac.seeds = seedResolver.apply(line);
                    } else if (line.startsWith("seed-to-soil map:")) {
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("soil-to-fertilizer map:")) {
                        almanac.seedToSoilMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("fertilizer-to-water map:")) {
                        almanac.soilToFertilizerMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("water-to-light map:")) {
                        almanac.fertilizerToWaterMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("light-to-temperature map:")) {
                        almanac.waterToLightMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("temperature-to-humidity map:")) {
                        almanac.lightToTemperatureMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (line.startsWith("humidity-to-location map:")) {
                        almanac.temperatureToHumidityMap = almanacMap.get();
                        almanacMap.set(new AlmanacMap());
                    } else if (!line.isBlank()) {
                        String[] values = line.trim().split("\\s");
                        almanacMap.get().addEntry(new AlmanacMapEntry(
                                Long.parseLong(values[1].trim()),
                                Long.parseLong(values[0].trim()),
                                Long.parseLong(values[2].trim()))
                        );
                    }
                });
        almanac.humidityToLocationMap = almanacMap.get();
        return almanac;
    }


    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private static class Almanac {
        private List<Long> seeds = new ArrayList<>();
        private AlmanacMap seedToSoilMap;
        private AlmanacMap soilToFertilizerMap;
        private AlmanacMap fertilizerToWaterMap;
        private AlmanacMap waterToLightMap;
        private AlmanacMap lightToTemperatureMap;
        private AlmanacMap temperatureToHumidityMap;
        private AlmanacMap humidityToLocationMap;
    }

    private static class AlmanacMap {
        private final List<AlmanacMapEntry> almanacMapEntries = new ArrayList<>();

        public void addEntry(AlmanacMapEntry almanacMapEntry) {
            almanacMapEntries.add(almanacMapEntry);
        }

        public long getDestination(long source) {
            return almanacMapEntries.stream()
                    .filter(t -> t.hasRecord(source))
                    .map(t -> t.getValue(source))
                    .findFirst()
                    .orElse(source);
        }
    }

    private record SeedEntry(long source, long length) {}

    private record AlmanacMapEntry(long source, long destination, long length) {
        public  boolean hasRecord(long id) {
            return id >= source && id <= source + length;
        }

        public long getValue(long id) {
            if (!hasRecord(id)) {
                throw new RuntimeException("id not found in map");
            }
            return destination + (id - source);
        }
    }
}
