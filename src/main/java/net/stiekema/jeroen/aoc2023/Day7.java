package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test: " + calculatePart1Test());
        System.out.println("Part 1: " + calculatePart1());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7-test.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7.txt");
        return calculatePart1(resource);
    }

    private static long calculatePart1(URL resource) throws URISyntaxException, IOException {
        long result = 0;
        List<Hand> hands = getLines(resource)
                .map(Day7::parseHand)
                .sorted()
                .toList();
        for (int i = 1; i <= hands.size(); i++) {
            Hand hand = hands.get(i-1);
            result += hand.bid() * i;
        }
        return result;
    }

    private static Hand parseHand(String hand) {
        String[] split = hand.split("\\s+");
        List<Card> cards = split[0].trim().chars()
                .mapToObj(t -> (char) t)
                .map(Card::new)
                .toList();
        return new Hand(cards, Long.parseLong(split[1].trim()));
    }

    private static Stream<String> getLines(URL resource) throws URISyntaxException, IOException {
        return Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
    }

    private record Hand(List<Card> cards, long bid) implements Comparable<Hand> {

        public int getHandType() {
            List<Long> cardAmounts = this.cards.stream()
                    .collect(Collectors.groupingBy(Card::getStrength, Collectors.counting()))
                    .values().stream()
                    .sorted(Comparator.reverseOrder())
                    .toList();
            if (cardAmounts.get(0) == 5) {
                return 7;
            } else if (cardAmounts.get(0) == 4) {
                return 6;
            } else if (cardAmounts.size() == 2) {
                return 5;
            } else if (cardAmounts.get(0) == 3) {
                return 4;
            } else if (cardAmounts.get(0) == 2 && cardAmounts.get(1) == 2) {
                return 3;
            } else if (cardAmounts.get(0) == 2) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public int compareTo(Hand o) {
            int typeCompare = Integer.compare(this.getHandType(), o.getHandType());
            if (typeCompare != 0) {
                return typeCompare;
            }
            for (int i = 0; i < 5; i++) {
                int cardCompare = this.cards.get(i).compareTo(o.cards.get(i));
                if (cardCompare != 0) {
                    return cardCompare;
                }
            }
            return 0;
        }
    }

    private record Card(char c) implements Comparable<Card> {

        @Override
        public int compareTo(Card o) {
            return Integer.compare(this.getStrength(), o.getStrength());
        }

        public int getStrength() {
            if (c >= '2' && c <= '9') {
                return c - '2';
            } else {
                switch (c) {
                    case 'T':
                        return 10;
                    case 'J':
                        return 11;
                    case 'Q':
                        return 12;
                    case 'K':
                        return 13;
                    case 'A':
                        return 14;
                }
            }
            return 0;
        }
    }
}
