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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("part 1 test: " + calculatePart1Test());
        System.out.println("part 1: " + calculatePart1());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4-1-test.txt");
        return calculateTotalScore(resource);
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4.txt");
        return calculateTotalScore(resource);
    }

    private static long calculateTotalScore(URL resource) throws URISyntaxException, IOException {
        return getLines(resource)
                .map(Day4::convertToCard)
                .map(Card::calculateScore)
                .reduce(Long::sum)
                .orElse(0L);


    }

    private static Card convertToCard(String line) {
        Pattern pattern = Pattern.compile("^Card\\s+([0-9]+):(.*?)\\|(.*?)$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int cardNr = Integer.parseInt(matcher.group(1));
            String numbers = matcher.group(2);
            String winningNumbers = matcher.group(3);
            return new Card(cardNr,
                    Arrays.stream(numbers.trim().split("\\s+"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList()),
                    Arrays.stream(winningNumbers.trim().split("\\s+"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList())
            );
        } else {
            throw new RuntimeException("can't convert line to card: " + line);
        }
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    public record Card (Integer cardNr, List<Integer> numbers, List<Integer> winningNumbers) {
        public long calculateScore() {
            long nrOfWinningNumbers = numbers.stream()
                    .filter(winningNumbers::contains)
                    .count();
            long score = nrOfWinningNumbers > 0 ? (long) Math.pow(2, (nrOfWinningNumbers - 1)) : 0;
            return score;
        }
    }
}
