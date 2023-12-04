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
        System.out.println("part 2 test: " + calculatePart2Test());
        System.out.println("part 2: " + calculatePart2());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4-1-test.txt");
        return calculateTotalScore(resource);
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4.txt");
        return calculateTotalScore(resource);
    }

    private static long calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4-1-test.txt");
        return calculateTotalNrOfCards(resource);
    }

    private static long calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day4.txt");
        return calculateTotalNrOfCards(resource);
    }

    private static long calculateTotalScore(URL resource) throws URISyntaxException, IOException {
        return getLines(resource)
                .map(Day4::convertToCard)
                .map(Card::calculateScore)
                .reduce(Long::sum)
                .orElse(0L);
    }

    private static long calculateTotalNrOfCards(URL resource) throws URISyntaxException, IOException {
        List<Card> cards = getLines(resource)
                .map(Day4::convertToCard)
                .collect(Collectors.toList());

        long totalCards = 0;
        for (int cardId = 0; cardId < cards.size(); cardId++) {
            totalCards += calculateTotalNrOfCards(cardId, cards);
        }
        return totalCards;
    }

    private static long calculateTotalNrOfCards(int cardId, List<Card> cards) {
        long winningNumbers = cards.get(cardId).calculateWinningNumbers();
        long result = 1;
        for (int i = 1; i <= winningNumbers; i++) {
            result += calculateTotalNrOfCards(cardId + i, cards);
        }
        return result;
    }

    private static Card convertToCard(String line) {
        Pattern pattern = Pattern.compile("^Card\\s+([0-9]+):(.*?)\\|(.*?)$");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String numbers = matcher.group(2);
            String winningNumbers = matcher.group(3);
            return new Card(
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

    public record Card (List<Integer> numbers, List<Integer> winningNumbers) {

        public long calculateWinningNumbers() {
            return numbers.stream()
                    .filter(winningNumbers::contains)
                    .count();
        }

        public long calculateScore() {
            long nrOfWinningNumbers = calculateWinningNumbers();
            return nrOfWinningNumbers > 0 ? (long) Math.pow(2, (nrOfWinningNumbers - 1)) : 0;
        }
    }
}
