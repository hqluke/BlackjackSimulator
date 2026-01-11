import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetermineSideBetPayout {
    
    public static double calculatePairPayout(Hand hand, double pairBet) {
        if (pairBet <= 0) {
            return 0;
        }
        
        if (hand.isPerfectPair()) {
            return pairBet * 30; // Perfect Pair pays 30:1
        } else if (hand.isSameColorPair()) {
            return pairBet * 10; // Same Color Pair pays 10:1
        } else if (hand.isMixedPair()) {
            return pairBet * 5; // Mixed Pair pays 5:1
        } else {
            return 0; // No pair, lose bet
        }
    }

    public static double calculateTwentyOnePlusThreePayout(Hand playerHand, Card dealerUpCard, double betAmount) {
        if (betAmount <= 0 || playerHand.getCards().size() != 2 || dealerUpCard == null) {
            return 0;
        }
        
        List<Card> threeCards = new ArrayList<>();
        threeCards.addAll(playerHand.getCards());
        threeCards.add(dealerUpCard);
        
        if (isSuitedTrips(threeCards)) {
            return betAmount * 100; // Suited Trips pays 100:1
        } else if (isStraightFlush(threeCards)) {
            return betAmount * 40; // Straight Flush pays 40:1
        } else if (isThreeOfAKind(threeCards)) {
            return betAmount * 30; // Three of a Kind pays 30:1
        } else if (isStraight(threeCards)) {
            return betAmount * 10; // Straight pays 10:1
        } else if (isFlush(threeCards)) {
            return betAmount * 5; // Flush pays 5:1
        } else {
            return 0;
        }
    }

    private static boolean isSuitedTrips(List<Card> cards) {
        // all three cards same rank AND same suit
        return cards.get(0).getRank() == cards.get(1).getRank() &&
               cards.get(0).getRank() == cards.get(2).getRank() &&
               cards.get(0).getSuit() == cards.get(1).getSuit() &&
               cards.get(0).getSuit() == cards.get(2).getSuit();
    }

    private static boolean isStraightFlush(List<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    private static boolean isThreeOfAKind(List<Card> cards) {
        // all three cards same rank (but not necessarily same suit)
        return cards.get(0).getRank() == cards.get(1).getRank() &&
               cards.get(0).getRank() == cards.get(2).getRank();
    }

    private static boolean isStraight(List<Card> cards) {
        List<Integer> values = new ArrayList<>();
        
        for (Card card : cards) {
            if (card.getRank() == Card.Rank.ACE) {
                // Ace can be 1 or 14
                values.add(1);
                values.add(14);
            } else if (card.getRank() == Card.Rank.JACK) {
                values.add(11);
            } else if (card.getRank() == Card.Rank.QUEEN) {
                values.add(12);
            } else if (card.getRank() == Card.Rank.KING) {
                values.add(13);
            } else {
                values.add(card.getValue());
            }
        }
        
        // try all combinations if Ace is present
        if (values.size() > 3) {
            // Ace present - check both Ace-low and Ace-high
            // Ace-low: A-2-3
            if (values.contains(1) && values.contains(2) && values.contains(3)) {
                return true;
            }
            // ce-high: Q-K-A
            if (values.contains(12) && values.contains(13) && values.contains(14)) {
                return true;
            }
            // check other combinations with Ace as 1
            List<Integer> aceLow = new ArrayList<>();
            for (Card card : cards) {
                if (card.getRank() == Card.Rank.ACE) {
                    aceLow.add(1);
                } else if (card.getRank() == Card.Rank.JACK) {
                    aceLow.add(11);
                } else if (card.getRank() == Card.Rank.QUEEN) {
                    aceLow.add(12);
                } else if (card.getRank() == Card.Rank.KING) {
                    aceLow.add(13);
                } else {
                    aceLow.add(card.getValue());
                }
            }
            Collections.sort(aceLow);
            if (aceLow.get(2) - aceLow.get(1) == 1 && aceLow.get(1) - aceLow.get(0) == 1) {
                return true;
            }
            
            // check with Ace as 14
            List<Integer> aceHigh = new ArrayList<>();
            for (Card card : cards) {
                if (card.getRank() == Card.Rank.ACE) {
                    aceHigh.add(14);
                } else if (card.getRank() == Card.Rank.JACK) {
                    aceHigh.add(11);
                } else if (card.getRank() == Card.Rank.QUEEN) {
                    aceHigh.add(12);
                } else if (card.getRank() == Card.Rank.KING) {
                    aceHigh.add(13);
                } else {
                    aceHigh.add(card.getValue());
                }
            }
            Collections.sort(aceHigh);
            return aceHigh.get(2) - aceHigh.get(1) == 1 && aceHigh.get(1) - aceHigh.get(0) == 1;
        } else {
            // no Ace - simple check
            Collections.sort(values);
            return values.get(2) - values.get(1) == 1 && values.get(1) - values.get(0) == 1;
        }
    }

    private static boolean isFlush(List<Card> cards) {
        // all three cards same suit
        Card.Suit suit = cards.get(0).getSuit();
        return cards.get(1).getSuit() == suit && cards.get(2).getSuit() == suit;
    }
}