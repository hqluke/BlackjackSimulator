public interface GUIEventListener {
    /**
     * Called by GUI when it needs to create a new game
     * @param numDecks Number of decks to use
     * @param startingMoney Starting money for the player
     * @param minimumBet Minimum bet amount
     */
    void onCreateGameRequested(int numDecks, double startingMoney, double minimumBet);
    
    /**
     * Called by GUI after the initial deal animation completes
     */
    void onInitialDealComplete();
    
    /**
     * Called by GUI after a hit animation completes
     * @param handIndex The hand that was hit
     */
    void onHitAnimationComplete(int handIndex);
    
    /**
     * Called by GUI after a double down animation completes
     * @param handIndex The hand that was doubled
     */
    void onDoubleAnimationComplete(int handIndex);
    
    /**
     * Called by GUI after dealer reveal animation completes
     */
    void onDealerRevealComplete();
    
    /**
     * Called by GUI after each dealer hit animation completes
     */
    void onDealerHitAnimationComplete();

    /**
     * Called by GUI when side bets are placed
     * @param pairBet Amount placed on Pair side bet
     * @param twentyOnePlusThreeBet Amount placed on 21+3 side bet
     */
    void onSideBetsPlaced(double pairBet, double twentyOnePlusThreeBet, boolean rememberSideBets);

}