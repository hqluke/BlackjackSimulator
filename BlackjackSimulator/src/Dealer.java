public class Dealer {
	//wip very same to player. need to lookout for isDealer & hideCard in Hand class
	
	private Hand hand;
    
    public Dealer() {
        this.hand = new Hand(true);
    }
    
    public void addCard(Card card) {
        hand.addCard(card);
    }
    
    public Hand getHand() {
        return hand;
    }
    
    public void hideSecondCard() {
        hand.setHideSecondCard(true);
    }
    
    public void revealCards() {
        hand.setHideSecondCard(false);
    }
    
    public boolean mustHit() {
        // some casinos hit on soft 17, but we'll stand on all 17s
        return hand.getValue() < 17;
    }
    
    public boolean isBust() {
        return hand.isBust();
    }
    
    public boolean isBlackjack() {
        return hand.isBlackjack();
    }
    
    public int getValue() {
        return hand.getValue();
    }
    
    public void clearHand() {
        hand.clear();
    }



}
