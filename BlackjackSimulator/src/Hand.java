import java.util.*;
public class Hand {
    private List<Card> cards;
    private boolean isDealer;
    private boolean hideCard;
    
    public Hand(boolean isDealer) {
        this.cards = new ArrayList<>();
        this.isDealer = isDealer;
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
    
    public boolean canSplit() {
        return cards.size() == 2 && 
               cards.get(0).getRank() == cards.get(1).getRank();
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
