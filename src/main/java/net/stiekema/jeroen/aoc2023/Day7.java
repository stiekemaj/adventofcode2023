package net.stiekema.jeroen.aoc2023;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Part 1 test: " + calculatePart1Test());
        System.out.println("Part 1: " + calculatePart1());
        System.out.println("Part 2 test: " + calculatePart2Test());
        System.out.println("Part 2: " + calculatePart2());
    }

    private static long calculatePart1Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7-test.txt");
        return calculate(resource, getPart1Comparator());
    }

    private static long calculatePart1() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7.txt");
        return calculate(resource, getPart1Comparator());
    }

    private static long calculatePart2Test() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7-test.txt");
        return calculate(resource, getPart2Comparator());
    }

    private static long calculatePart2() throws URISyntaxException, IOException {
        URL resource = Day3.class.getResource("/day7.txt");
        return calculate(resource, getPart2Comparator());
    }

    private static long calculate(URL resource, Comparator<Hand> comparator) throws URISyntaxException, IOException {
        long result = 0;
        List<Hand> hands = getLines(resource)
                .map(Day7::parseHand)
                .sorted(comparator)
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

    private static Comparator<Hand> getPart1Comparator() {
        return (o1, o2) -> {
            int compare = Integer.compare(o1.getHandType(), o2.getHandType());
            if (compare != 0) return compare;
            return getHandStrengthComparator(Comparator.comparingInt(Card::getStrength)).compare(o1, o2);
        };
    }

    private static Comparator<Hand> getPart2Comparator() {
        return (o1, o2) -> {
            int compare = Integer.compare(o1.getHandTypeWithJoker(), o2.getHandTypeWithJoker());
            if (compare != 0) return compare;
            return getHandStrengthComparator(Comparator.comparingInt(Card::getStrengthWithJoker)).compare(o1, o2);
        };
    }

    private static Comparator<Hand> getHandStrengthComparator(Comparator<Card> cardComparator) {
        return (o1, o2) -> IntStream.range(0, 5)
                .mapToObj(i -> cardComparator.compare(o1.cards.get(i), o2.cards.get(i)))
                .filter(i -> i != 0)
                .findFirst()
                .orElse(0);
    }

    private record Hand(List<Card> cards, long bid) {

        public int getHandType() {
            List<Integer> cardAmounts = this.cards.stream()
                    .collect(Collectors.groupingBy(card -> Card.STRENGTH_ORDER.indexOf(card.type), Collectors.counting()))
                    .values().stream()
                    .sorted(Comparator.reverseOrder())
                    .map(Long::intValue)
                    .toList();
            return getHandType(cardAmounts);
        }

        public int getHandTypeWithJoker() {
            int nrOfJokers = (int) this.cards.stream().filter(t -> t.type == 'J').count();
            AtomicBoolean jokersAdded = new AtomicBoolean(false);
            List<Integer> cardAmounts;
            if (nrOfJokers == 5) {
                cardAmounts = List.of(5);
            } else {
                cardAmounts = this.cards.stream()
                        .filter(t -> t.type != 'J')
                        .collect(Collectors.groupingBy(card -> Card.STRENGTH_ORDER.indexOf(card.type), Collectors.counting()))
                        .values().stream()
                        .sorted(Comparator.reverseOrder())
                        .map(t -> {
                            if (jokersAdded.get()) {
                                return t.intValue();
                            } else {
                                jokersAdded.set(true);
                                return t.intValue() + nrOfJokers;
                            }
                        })
                        .toList();
            }

            return getHandType(cardAmounts);
        }

        private static int getHandType(List<Integer> cardAmounts) {
            return switch (cardAmounts.get(0)) {
                case 5 -> 7;
                case 4 -> 6;
                case 3 -> cardAmounts.get(1) == 2L ? 5 : 4;
                case 2 -> cardAmounts.get(1) == 2L ? 3 : 2;
                default -> 1;
            };
        }
    }

    private record Card(char type) {
        private static final List<Character> STRENGTH_ORDER = List.of('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
        private static final List<Character> STRENGTH_WITH_JOKER_ORDER = List.of('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A');

        public int getStrength() {
            return STRENGTH_ORDER.indexOf(type);
        }

        public int getStrengthWithJoker() {
            return STRENGTH_WITH_JOKER_ORDER.indexOf(type);
        }
    }
}
