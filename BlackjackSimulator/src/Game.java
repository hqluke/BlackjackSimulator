public class Game {
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private GameStateListener listener;
    private int numDecks;
    private double minimumBet;
    private int runningCount;
    private boolean roundInProgress;
    private Card dealerHiddenCard = null;
    
    public Game(int numDecks, double startingMoney, double minimumBet) {
        this.numDecks = numDecks;
        this.minimumBet = minimumBet;
        this.deck = new Deck(numDecks);
        this.player = new Player(startingMoney);
        this.dealer = new Dealer();
        this.runningCount = 0;
        this.roundInProgress = false;
    }
    
    public void setGameStateListener(GameStateListener listener) {
        this.listener = listener;
    }
    
    // start a new round
    public void startRound(double betAmount) {
        if (roundInProgress) {
            notifyError("Round already in progress");
            return;
        }
        
        if (betAmount < minimumBet) {
            notifyError("Bet below minimum: $" + minimumBet);
            return;
        }
        
        if (!player.canAfford(betAmount)) {
            notifyError("Insufficient funds");
            return;
        }
        
        // clear previous round
        player.clearHands();
        dealer.clearHand();
        
        // place bet and create initial hand
        player.placeBet(betAmount);
        
        roundInProgress = true;
        
        if (listener != null) {
            listener.onRoundStart();
        }
        
        // check if deck needs reshuffling AFTER onRoundStart
        if (deck.getDecksRemaining() < numDecks * 0.25) {
            reshuffleDeck();
        }
        
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
        dealerHiddenCard = drawCard();
        dealer.addCard(dealerHiddenCard);
        dealer.hideSecondCard();
        
        // notify GUI to animate the deal
        if (listener != null) {
            listener.onInitialDeal(player, dealer);
        }
    }
    
    // called by GUI after initial deal animation completes
    public void checkForBlackjacks() {
        Hand playerHand = player.getHand(0);
        boolean playerBJ = playerHand.isBlackjack();
        boolean dealerBJ = dealer.isBlackjack();
        
        if (playerBJ || dealerBJ) {
            dealer.revealCards();
            resolveBlackjacks(playerBJ, dealerBJ);
            
            if (listener != null) {
                listener.onRoundEnd(player, dealer, player.getBets());
            }
            roundInProgress = false;
        } else {
            // normal play - notify GUI to show action buttons
            if (listener != null) {
                listener.onPlayerTurn(player, 0);
            }
        }
    }
    
    private void resolveBlackjacks(boolean playerBJ, boolean dealerBJ) {
        Bet bet = player.getBet(0);
        
        if (playerBJ && dealerBJ) {
            bet.setResult(Bet.BetResult.PUSH);
        } else if (playerBJ) {
            bet.setResult(Bet.BetResult.BLACKJACK);
        } else {
            bet.setResult(Bet.BetResult.LOSE);
        }
        
        payoutBets();
    }
    
    // player hits - GUI handles animation, then calls completeHit
    public void hit(int handIndex) {
        if (!roundInProgress) {
            notifyError("No round in progress");
            return;
        }
        
        Hand hand = player.getHand(handIndex);
        if (hand == null) {
            notifyError("Invalid hand index");
            return;
        }
        
        // GUI will handle animation and call completeHit
    }
    
    // called by GUI after hit animation completes
    public void completeHit(int handIndex) {
        Card card = drawCard();
        player.addCardToHand(card, handIndex);
        Hand hand = player.getHand(handIndex);
        
        // check if player busted
        if (hand.isBust()) {
            player.getBet(handIndex).setResult(Bet.BetResult.LOSE);
            handleHandCompletion(handIndex);
        }
        // GUI will update display and buttons
    }
    
    // player stands 
    public void stand(int handIndex) {
        if (!roundInProgress) {
            notifyError("No round in progress");
            return;
        }
        
        handleHandCompletion(handIndex);
    }
    
    // player doubles down - GUI handles animation, then calls completeDouble
    public void doubleDown(int handIndex) {
        if (!roundInProgress) {
            notifyError("No round in progress");
            return;
        }
        
        try {
            player.doubleDown(handIndex);
            // GUI will handle animation and call completeDouble
        } catch (IllegalArgumentException e) {
            notifyError(e.getMessage());
        }
    }
    
    // called by GUI after double animation completes
    public void completeDouble(int handIndex) {
        Card card = drawCard();
        player.addCardToHand(card, handIndex);
        Hand hand = player.getHand(handIndex);
        
        // if busted, mark as loss
        if (hand.isBust()) {
            player.getBet(handIndex).setResult(Bet.BetResult.LOSE);
        }
        
        // automatically complete this hand
        handleHandCompletion(handIndex);
    }
    
    // player splits
    public void split(int handIndex) {
        if (!roundInProgress) {
            notifyError("No round in progress");
            return;
        }
        
        try {
            player.split(handIndex);
            
            // deal one card to each of the split hands
            Card card1 = drawCard();
            player.addCardToHand(card1, handIndex);
            
            Card card2 = drawCard();
            player.addCardToHand(card2, handIndex + 1);
            
            // GUI will update display
        } catch (IllegalArgumentException e) {
            notifyError(e.getMessage());
        }
    }
    
    // handle completion of a hand (either stood or busted)
    private void handleHandCompletion(int handIndex) {
        int nextHandIndex = handIndex + 1;
        
        if (nextHandIndex < player.getNumHands()) {
            // move to next hand
            player.setCurrentHandIndex(nextHandIndex);
            if (listener != null) {
                listener.onPlayerTurn(player, nextHandIndex);
            }
        } else {
            // all player hands done - check if dealer needs to play
            if (allHandsBusted()) {
                // all hands busted, no need for dealer to play
                resolveAllHands();
                if (listener != null) {
                    listener.onRoundEnd(player, dealer, player.getBets());
                }
                roundInProgress = false;
            } else {
                // at least one hand is still active, dealer's turn
                if (listener != null) {
                    listener.onDealerTurn(dealer);
                }
            }
        }
    }
    
    // check if all player hands have busted
    private boolean allHandsBusted() {
        for (int i = 0; i < player.getNumHands(); i++) {
            Hand hand = player.getHand(i);
            if (!hand.isBust()) {
                return false;
            }
        }
        return true;
    }
    
    // start dealer's turn - called by GUI
    public void startDealerPlay() {
        dealer.revealCards();
        if (dealerHiddenCard != null) {
        runningCount += dealerHiddenCard.getCountValue();
        dealerHiddenCard = null;
    }
        // GUI will handle reveal animation and then call dealerDrawCards
    }
    
    // dealer draws cards - called by GUI after reveal
    public void dealerDrawCards() {
        // GUI handles animation, checking mustHit() and calling dealerHit() for each card
    }
    
    // called by GUI for each dealer card drawn
    public void dealerHit() {
        Card card = drawCard();
        dealer.addCard(card);
    }
    
    // called by GUI when dealer is done
    public void finalizeDealerPlay() {
        resolveAllHands();
        
        if (listener != null) {
            listener.onRoundEnd(player, dealer, player.getBets());
        }
        
        roundInProgress = false;
    }
    
    // resolve all player hands against dealer
    private void resolveAllHands() {
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
        
        payoutBets();
    }
    
    // pay out all bets based on results
    private void payoutBets() {
        for (int i = 0; i < player.getBets().size(); i++) {
            Bet bet = player.getBets().get(i);
            double payout = bet.getPayout();
            if (payout > 0) {
                player.addMoney(payout);
            }
        }
        
        if (listener != null) {
            listener.onMoneyChanged(player.getMoney());
        }
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
        
        if (listener != null) {
            listener.onDeckReshuffled(numDecks, deck.getCardsRemaining());
        }
    }
    
    private void notifyError(String message) {
        if (listener != null) {
            listener.onError(message);
        }
    }

    // todo: implement insurance betting
    // private void placeInsurance() {

    //     Bet originalBet = player.getBet(0);
    //     double insuranceAmount = originalBet.getAmount() / 2.0;

    //     player.placeInsurance(insuranceAmount);
    // }
    
    // getters
    public Player getPlayer() { return player; }
    public Dealer getDealer() { return dealer; }
    public Deck getDeck() { return deck; }
    public int getRunningCount() { return runningCount; }
    public double getTrueCount() {
        double decksRemaining = deck.getDecksRemaining();
        return decksRemaining > 0 ? runningCount / decksRemaining : 0;
    }
    public boolean isRoundInProgress() { return roundInProgress; }
    public double getMinimumBet() { return minimumBet; }
    public boolean canContinuePlaying() { return player.getMoney() >= minimumBet; }
    public Card peekNextCard() { return deck.peek(); }
}