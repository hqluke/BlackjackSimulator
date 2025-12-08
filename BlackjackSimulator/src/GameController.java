public class GameController implements GUIEventListener {
    private Game game;
    private GameStateListener guiListener;
    
    public GameController(GameStateListener guiListener) {
        this.guiListener = guiListener;
    }
    
    @Override
    public void onCreateGameRequested(int numDecks, double startingMoney, double minimumBet) {
        // create the game
        game = new Game(numDecks, startingMoney, minimumBet);
        game.setGameStateListener(guiListener);
        
        // notify GUI that game is created with initial state
        if (guiListener != null) {
            guiListener.onGameCreated(
                game.getRunningCount(),
                game.getTrueCount(),
                game.getPlayer().getMoney(),
                game.getDeck().getNumDecks(),
                game.getDeck().getCardsRemaining(),
                game.getMinimumBet()
            );
        }
    }
    
    @Override
    public void onInitialDealComplete() {
        if (game != null) {
            game.onInitialDealComplete();
        }
    }
    
    @Override
    public void onHitAnimationComplete(int handIndex) {
        if (game != null) {
            game.onHitAnimationComplete(handIndex);
        }
    }
    
    @Override
    public void onDoubleAnimationComplete(int handIndex) {
        if (game != null) {
            game.onDoubleAnimationComplete(handIndex);
        }
    }
    
    @Override
    public void onDealerRevealComplete() {
        if (game != null) {
            game.onDealerRevealComplete();
        }
    }
    
    @Override
    public void onDealerHitAnimationComplete() {
        if (game != null) {
            game.onDealerHitAnimationComplete();
        }
    }

    @Override
    public void onSideBetsPlaced(double pairBet, double twentyOnePlusThreeBet, boolean rememberSideBets) {
        if (game != null) {
            game.onSideBetsPlaced(pairBet, twentyOnePlusThreeBet, 0, rememberSideBets);
        }
    }
    // game action methods
    public void startRound(double betAmount) {
        if (game != null) {
            game.startRound(betAmount);
        }
    }

    public int getCardsRemaining() {
        return game.getDeck().getCardsRemaining();
    }
    
    public void hit(int handIndex) {
        if (game != null) {
            game.hit(handIndex);
        }
    }
    
    public void stand(int handIndex) {
        if (game != null) {
            game.stand(handIndex);
        }
    }
    
    public void doubleDown(int handIndex) {
        if (game != null) {
            game.doubleDown(handIndex);
        }
    }
    
    public void split(int handIndex) {
        if (game != null) {
            game.split(handIndex);
        }
    }
    
    public void startDealerPlay() {
        if (game != null) {
            game.startDealerPlay();
        }
    }
    
    public void dealerHit() {
        if (game != null) {
            game.dealerHit();
        }
    }
    
    public void finalizeDealerPlay() {
        if (game != null) {
            game.finalizeDealerPlay();
        }
    }
    
    // insurance methods
    public void acceptedInsurance(boolean accepted) {
        if (game != null) {
            game.acceptedInsurance(accepted);
        }
    }
    
    // betting methods
    public void setCustomBetAmount(int amount) {
        if (game != null) {
            game.setCustomBetAmount(amount);
        }
    }
    
    public int getCustomBetAmount() {
        return game != null ? game.getCustomBetAmount() : 0;
    }

    public double getPairBetAmount() {
        return game != null ? game.getPairBetAmount() : 0.0;
    }

    public double getTwentyOnePlusThreeBetAmount() {
        return game != null ? game.getTwentyOnePlusThreeBetAmount() : 0.0;
    }

    // query methods
    public Card peekNextCard() {
        return game != null ? game.peekNextCard() : null;
    }
    
    public boolean isRoundInProgress() {
        return game != null && game.isRoundInProgress();
    }
    
    public boolean canContinuePlaying() {
        return game != null && game.canContinuePlaying();
    }

    public boolean areSideBetsRemembered() {
        return game != null && game.areSideBetsRemembered();
    }

    public boolean isPairBetPlaced() {
        return game != null && game.isPairBetPlaced();
    }

    public boolean is21Plus3BetPlaced() {
        return game != null && game.is21Plus3BetPlaced();
    }

    public boolean isBlockAccessToSideBets() {
        return game != null && game.isBlockAccessToSideBets();
    }
    
    // player data access
    public Player getPlayer() {
        return game != null ? game.getPlayer() : null;
    }
    
    public Dealer getDealer() {
        return game != null ? game.getDealer() : null;
    }
    
    // card counting access
    public int getRunningCount() {
        return game != null ? game.getRunningCount() : 0;
    }
    
    public double getTrueCount() {
        return game != null ? game.getTrueCount() : 0.0;
    }
    
    // dealer methods
    public boolean dealerMustHit() {
        return game != null && game.getDealer() != null && game.getDealer().mustHit();
    }
    
    public boolean dealerIsBust() {
        return game != null && game.getDealer() != null && game.getDealer().isBust();
    }
}