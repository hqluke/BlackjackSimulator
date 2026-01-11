import java.util.*;
public class Hand {
    private List<Card> cards;
    private boolean hideCard;
    
    public Hand() {
        this.cards = new ArrayList<>();
        this.hideCard = false;
    }
    
    public void addCard(Card card) {
        cards.add(card);
    }
    
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    public int getValue() {
        int value = 0;
        int aces = 0;
        
        for (Card card : cards) {
            if (card.getRank() == Card.Rank.ACE) {
                aces++;
                value += 11;
            } else {
                value += card.getValue();
            }
        }
        
        
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        
        return value;
    }
    
    public boolean isBust() {
        return getValue() > 21;
    }
    
    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isFirstCardAce() {
        return !cards.isEmpty() && cards.get(0).getRank() == Card.Rank.ACE;
    }   
    
    public boolean canSplit() {
        return cards.size() == 2 && 
               cards.get(0).getRank() == cards.get(1).getRank();
    }

    public boolean isPerfectPair() {
        return canSplit() && 
               cards.get(0).getSuit() == cards.get(1).getSuit();
    }

    public boolean isSameColorPair() {
        if(!canSplit()) {
            return false;
        }
        String Hearts = Card.Suit.HEARTS.getSymbol();
        String Diamonds = Card.Suit.DIAMONDS.getSymbol();
        String Clubs = Card.Suit.CLUBS.getSymbol();
        String Spades = Card.Suit.SPADES.getSymbol();

        String firstCardSuit = cards.get(0).getSuit().getSymbol();
        String secondCardSuit = cards.get(1).getSuit().getSymbol();

        boolean firstRed = firstCardSuit.equals(Hearts) || firstCardSuit.equals(Diamonds);
        boolean secondRed = secondCardSuit.equals(Hearts) || secondCardSuit.equals(Diamonds);
        boolean firstBlack = firstCardSuit.equals(Clubs) || firstCardSuit.equals(Spades);
        boolean secondBlack = secondCardSuit.equals(Clubs) || secondCardSuit.equals(Spades);

        return (firstRed && secondRed) || (firstBlack && secondBlack);
    }

    public boolean isMixedPair() {
        return canSplit() && !isPerfectPair() && !isSameColorPair();
    }
    
    public boolean canDouble() {
        return cards.size() == 2;
    }
    
    public void setHideSecondCard(boolean hide) {
        this.hideCard = hide;
    }
    
    public boolean isHideSecondCard() {
        return hideCard;
    }
    
    public void clear() {
        cards.clear();
        hideCard = false;
    }
}
