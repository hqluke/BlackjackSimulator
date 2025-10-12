import java.util.*;

public class Game {
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private GUI gui;
    private int numDecks;
    private double minimumBet;
    private int runningCount;
    private boolean roundInProgress;
    
    public Game(GUI gui, int numDecks, double startingMoney, double minimumBet) {
        this.gui = gui;
        this.numDecks = numDecks;
        this.minimumBet = minimumBet;
        this.deck = new Deck(numDecks);
        this.player = new Player(startingMoney);
        this.dealer = new Dealer();
        this.runningCount = 0;
        this.roundInProgress = false;
    }
    
    // start a new round
    public void startRound(double betAmount) {
        if (roundInProgress) {
            throw new IllegalStateException("Round already in progress");
        }
        
        if (betAmount < minimumBet) {
            throw new IllegalArgumentException("Bet below minimum: $" + minimumBet);
        }
        
        if (!player.canAfford(betAmount)) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // check if deck needs reshuffling (less than 25% remaining)
        if (deck.getDecksRemaining() < numDecks * 0.25) {
            reshuffleDeck();
        }
        
        // clear previous round
        player.clearHands();
        dealer.clearHand();
        
        // place bet and create initial hand
        player.placeBet(betAmount);
        
        roundInProgress = true;
        
        // deal initial cards
        dealInitialCards();
    }
    
    // deal the initial 4 cards (2 to player, 2 to dealer)
    private void dealInitialCards() {
        // player first card
        Card playerCard1 = drawCard();
        player.addCardToHand(playerCard1, 0);
        
        // dealer first card (visible)
        Card dealerCard1 = drawCard();
        dealer.addCard(dealerCard1);
        
        // player second card
        Card playerCard2 = drawCard();
        player.addCardToHand(playerCard2, 0);
        
        // dealer second card (hidden)
        Card dealerCard2 = drawCard();
        dealer.addCard(dealerCard2);
        dealer.hideSecondCard();
        
        // check for blackjacks
        checkInitialBlackjacks();
    }
    
    // check if either player or dealer has blackjack on initial deal
    private void checkInitialBlackjacks() {
        Hand playerHand = player.getHand(0);
        boolean playerBJ = playerHand.isBlackjack();
        boolean dealerBJ = dealer.isBlackjack();
        
        if (playerBJ || dealerBJ) {
            dealer.revealCards();
            resolveBlackjacks(playerBJ, dealerBJ);
            endRound();
        }
    }
    
    // handle blackjack scenarios
    private void resolveBlackjacks(boolean playerBJ, boolean dealerBJ) {
        Bet bet = player.getBet(0);
        
        if (playerBJ && dealerBJ) {
            bet.setResult(Bet.BetResult.PUSH);
        } else if (playerBJ) {
            bet.setResult(Bet.BetResult.BLACKJACK);
        } else {
            bet.setResult(Bet.BetResult.LOSE);
        }
    }
    
    // player hits
    public void hit(int handIndex) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        Hand hand = player.getHand(handIndex);
        if (hand == null) {
            throw new IllegalArgumentException("Invalid hand index");
        }
        
        Card card = drawCard();
        player.addCardToHand(card, handIndex);
        
        // check if player busted
        if (hand.isBust()) {
            player.getBet(handIndex).setResult(Bet.BetResult.LOSE);
            
            // move to next hand or end if all hands done
            int nextHandIndex = handIndex + 1;
            if (nextHandIndex < player.getNumHands()) {
                player.setCurrentHandIndex(nextHandIndex);
            } else if (allHandsResolved()) {
                endRound();
            }
        }
    }
    
    // player stands 
    public void stand(int handIndex) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        // move to next hand if player has split
        int nextHandIndex = handIndex + 1;
        if (nextHandIndex < player.getNumHands()) {
            player.setCurrentHandIndex(nextHandIndex);
        }
    }
    
    // player doubles down
    public void doubleDown(int handIndex) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        player.doubleDown(handIndex);
        
        // draw exactly one more card
        Card card = drawCard();
        player.addCardToHand(card, handIndex);
        
        Hand hand = player.getHand(handIndex);
        
        // if busted, mark as loss
        if (hand.isBust()) {
            player.getBet(handIndex).setResult(Bet.BetResult.LOSE);
        }
        
        // automatically stand after double down
        stand(handIndex);
    }
    
    // player splits
    public void split(int handIndex) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        player.split(handIndex);
        
        // deal one card to each of the split hands
        Card card1 = drawCard();
        player.addCardToHand(card1, handIndex);
        
        Card card2 = drawCard();
        player.addCardToHand(card2, handIndex + 1);
    }
    
    // resolve all player hands against dealer
    private void setHandResult() {
        int dealerValue = dealer.getValue();
        boolean dealerBust = dealer.isBust();
        
        for (int i = 0; i < player.getNumHands(); i++) {
            Hand hand = player.getHand(i);
            Bet bet = player.getBet(i);
            
            // skip if already resolved (e.g., busted)
            if (bet.getResult() != Bet.BetResult.PENDING) {
                continue;
            }
            
            int playerValue = hand.getValue();
            
            if (dealerBust) {
                bet.setResult(Bet.BetResult.WIN);
            } else if (playerValue > dealerValue) {
                bet.setResult(Bet.BetResult.WIN);
            } else if (playerValue < dealerValue) {
                bet.setResult(Bet.BetResult.LOSE);
            } else {
                bet.setResult(Bet.BetResult.PUSH);
            }
        }
    }
    
    // check if all hands are resolved (busted or completed)
    private boolean allHandsResolved() {
        for (int i = 0; i < player.getNumHands(); i++) {
            Hand hand = player.getHand(i);
            Bet bet = player.getBet(i);
            if (!hand.isBust() && bet.getResult() == Bet.BetResult.PENDING) {
                return false;
            }
        }
        return true;
    }
    
    // end the round and pay out bets
    private void endRound() {
        payoutBets();
        roundInProgress = false;
    }
    
    // pay out all bets based on results
    private void payoutBets() {
        System.out.println("=== Paying out bets ===");
        for (int i = 0; i < player.getBets().size(); i++) {
            Bet bet = player.getBets().get(i);
            double payout = bet.getPayout();
            System.out.println("Hand " + (i+1) + ": Result=" + bet.getResult() + ", Bet=$" + bet.getAmount() + ", Payout=$" + payout);
            if (payout > 0) {
                player.addMoney(payout);
            }
        }
        System.out.println("Player money after payout: $" + player.getMoney());
    }
    
    // draw a card from the deck and update running count
    private Card drawCard() {
        if (deck.isEmpty()) {
            reshuffleDeck();
        }
        
        Card card = deck.drawCard();
        runningCount += card.getCountValue();
        return card;
    }
    
    // reshuffle the deck
    private void reshuffleDeck() {
        deck.reset();
        runningCount = 0;
    }
    
    // getters for game state
    public Player getPlayer() {
        return player;
    }
    
    public Dealer getDealer() {
        return dealer;
    }
    
    public Deck getDeck() {
        return deck;
    }
    
    public int getRunningCount() {
        return runningCount;
    }
    
    public double getTrueCount() {
        double decksRemaining = deck.getDecksRemaining();
        return decksRemaining > 0 ? runningCount / decksRemaining : 0;
    }
    
    public boolean isRoundInProgress() {
        return roundInProgress;
    }
    
    public double getMinimumBet() {
        return minimumBet;
    }
    
    // check if player has enough money to continue
    public boolean canContinuePlaying() {
        return player.getMoney() >= minimumBet;
    }

    public Card peekNextCard() {
        // returns the next card that will be dealt without removing it from deck
        return deck.peek();
    }

    public void dealerHit() {
        // dealer draws a card
        Card card = deck.drawCard();
        dealer.addCard(card);
        runningCount += card.getCountValue();
    }

    public boolean isPlayerDone() {
        // checks if all player hands are finished
        for (int i = 0; i < player.getNumHands(); i++) {
            Hand hand = player.getHand(i);
            Bet bet = player.getBet(i);
            
            // if any hand is still pending and not bust, player isn't done
            if (bet.getResult() == Bet.BetResult.PENDING && !hand.isBust()) {
                return false;
            }
        }
        return true;
    }

    public void finalizeRound() {
        setHandResult();
        endRound();
    }
        


}