import java.util.*;

public class Deck {
    private List<Card> cards;
    private int numDecks;
    private Random random;
    
    public Deck(int numDecks) {
        this.numDecks = numDecks;
        this.random = new Random();
        this.cards = new ArrayList<>();
        initializeDeck();
    }
    
    private void initializeDeck() {
        cards.clear();
        //based on num of decks
        for (int deck = 0; deck < numDecks; deck++) {
        	//goes through each suit and creates 1 of each card and adds it to the cards list
        	//like a for each loop
            for (Card.Suit suit : Card.Suit.values()) {
            	//adds the actual card value and count value
                for (Card.Rank rank : Card.Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
        shuffle();
    }
    
    public void shuffle() {
        Collections.shuffle(cards, random); //randomizes the whole cards list
    }
    
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    public int getCardsRemaining() {
        return cards.size();
    }
    
    public int getNumDecks() {
        return numDecks;
    }
    
    public double getDecksRemaining() {
        return cards.size() / 52.0; //rounds down
    }
    
    public void reset() {
        initializeDeck();
    }

    public Card drawCard() {
    if (cards.isEmpty()) {
        throw new IllegalStateException("Deck is empty");
    }
    return cards.remove(cards.size() - 1);
}
}
