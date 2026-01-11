
public class Card {
    public enum Suit {
    	
        HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");
        
        private String symbol;
        
        Suit(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    
    public enum Rank {
        TWO(2, 1), THREE(3, 1), FOUR(4, 1), FIVE(5, 1), SIX(6, 1),
        SEVEN(7, 0), EIGHT(8, 0), NINE(9, 0),
        TEN(10, -1), JACK(10, -1), QUEEN(10, -1), KING(10, -1), ACE(11, -1);
        
        private final int value;
        private final int countValue;
        
        Rank(int value, int countValue) {
            this.value = value;
            this.countValue = countValue;
        }
        
        public int getValue() {
            return value;
        }
        
        public int getCountValue() {
            return countValue;
        }
    }
    
    private Suit suit;
    private Rank rank;
    
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public int getValue() {
        return rank.getValue();
    }
    
    public int getCountValue() {
        return rank.getCountValue();
    }
    
}
    
    

