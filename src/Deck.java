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

    public void createTestSplitDeck() {
    cards.clear();
    
    // Create a deck that allows 3 splits in a row
    // Player gets: 8 8
    // Dealer gets 6 7
    
    // Add multiple 8s for player to keep splitting
    cards.add(new Card(Card.Suit.HEARTS, Card.Rank.TWO));   // player card 1
    cards.add(new Card(Card.Suit.SPADES, Card.Rank.SIX));     // dealer hidden card
    cards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.TWO)); // player card 2
    cards.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN));   // dealer up card
    cards.add(new Card(Card.Suit.CLUBS, Card.Rank.TWO));    // split card 1
    // cards.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));   // split card 2
    // cards.add(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));   // split card 3
    // cards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT)); // split card 4
    // cards.add(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));    // split card 5
    // cards.add(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));   // split card 6
    // cards.add(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));   // split card 7
    // cards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT)); // split card 8
    
    // Dealer cards

    
    
    // Add some filler cards for dealer/player hits
    // for (int i = 0; i < 10; i++) {
    //     cards.add(new Card(Card.Suit.HEARTS, Card.Rank.THREE));
    // }
    
    // Reverse the list since drawCard() removes from the end
    Collections.reverse(cards);
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

    public Card peek() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }

}
