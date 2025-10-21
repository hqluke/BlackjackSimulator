import java.util.*;

public interface GameStateListener {
    /**
     * Called when a new round starts after bet is placed
     */
    void onRoundStart();
    
    /**
     * Called after initial 4 cards are dealt
     * @param player The player object with current hand state
     * @param dealer The dealer object with current hand state
     */
    void onInitialDeal(Player player, Dealer dealer);
    
    /**
     * Called when it's the player's turn to act
     * @param player The player object
     * @param handIndex Which hand is active (for splits)
     */
    void onPlayerTurn(Player player, int handIndex);
    
    /**
     * Called when player's hand is updated (after hit/double)
     * @param player The player object
     * @param handIndex Which hand was updated
     */
    void onPlayerHandUpdated(Player player, int handIndex);
    
    /**
     * Called when dealer starts their turn
     * @param dealer The dealer object
     */
    void onDealerTurn(Dealer dealer);
    
    /**
     * Called when dealer should start drawing cards
     */
    void onDealerDrawing(Dealer dealer);
    
    /**
     * Called when the round ends
     * @param player The player object with final state
     * @param dealer The dealer object with final state
     * @param bets List of bets with results
     */
    void onRoundEnd(Player player, Dealer dealer, List<Bet> bets);
    
    /**
     * Called when player's money changes
     * @param newAmount The new money amount
     */
    void onMoneyChanged(double newAmount);
    
    /**
     * Called when deck is reshuffled
     * @param numDecks Number of decks in the shoe
     * @param cardsRemaining Number of cards after reshuffle
     */
    void onDeckReshuffled(int numDecks, int cardsRemaining);
    
    /**
     * Called when an error occurs (invalid bet, insufficient funds, etc.)
     * @param message Error message to display
     */
    void onError(String message);

    /**
     * Called when insurance is offered to the player
     */
    void onInsuranceOffer();
}