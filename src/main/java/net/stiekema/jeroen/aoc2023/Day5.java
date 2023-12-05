package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1Test());
        System.out.println("part 1: " + calculatePart1());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5-test.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day5.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart1(URL resource) throws URISyntaxException, IOException {
        Almanac almanac = parseAlmanac(resource);

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

    private static Almanac parseAlmanac(URL resource) throws URISyntaxException, IOException {
        Almanac almanac = new Almanac();
        AtomicReference<AlmanacMap> almanacMap = new AtomicReference<>();
        getLines(resource)
                .forEach(line -> {
                    if (line.startsWith("seeds:")) {
                        almanac.seeds = Arrays.stream(line.split(":")[1].trim().split(" "))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
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

        public void setSeeds(List<Long> seeds) {
            this.seeds = seeds;
        }
    }

    private static class AlmanacMap {
        private final SortedSet<AlmanacMapEntry> almanacMapEntries = new TreeSet<>();

        public void addEntry(AlmanacMapEntry almanacMapEntry) {
            almanacMapEntries.add(almanacMapEntry);
        }

        public long getDestination(long source) {
            return almanacMapEntries.stream()
                    .takeWhile(t -> t.source <= source)
                    .filter(t -> t.hasRecord(source))
                    .map(t -> t.getValue(source))
                    .findFirst()
                    .orElse(source);
        }
    }

    private record AlmanacMapEntry(long source, long destination, long length) implements Comparable<AlmanacMapEntry> {
        public  boolean hasRecord(long id) {
            return id >= source && id <= source + length;
        }

        public long getValue(long id) {
            if (!hasRecord(id)) {
                throw new RuntimeException("id not found in map");
            }
            return destination + (id - source);
        }

        @Override
        public int compareTo(AlmanacMapEntry o) {
            return Long.compare(this.source, o.source);
        }
    }
}
