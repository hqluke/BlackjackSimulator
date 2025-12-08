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
  private boolean insuranceOffered = false;
  private boolean insuranceResolved = false;
  private int customBetAmount = (int) minimumBet;
  private double pairBetAmount = 0.0;
  private boolean isPairBetPlaced = false;
  private double twentyOnePlusThreeBetAmount = 0.0;
  private boolean is21Plus3BetPlaced = false;
  private boolean areSideBetsRemembered = false;
  private boolean blockAccessToSideBets = false;

  public Game(int numDecks, double startingMoney, double minimumBet) {
    this.numDecks = numDecks;
    this.minimumBet = minimumBet;
    this.deck = new Deck(numDecks);
    // TODO: comment out below to run normal deck
    deck.createTestSplitDeck();
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

    // Check if player can afford main bet + side bets
    double totalNeeded = betAmount;
    if (isPairBetPlaced || is21Plus3BetPlaced) {
        totalNeeded += pairBetAmount + twentyOnePlusThreeBetAmount;
    }

    if (!player.canAfford(totalNeeded)) {
        notifyError("Insufficient funds for bet and side bets");
        return;
    }

    // clear previous round
    player.clearHands();
    dealer.clearHand();
    insuranceOffered = false;
    insuranceResolved = false;

    // place bet and create initial hand
    player.placeBet(betAmount);

    roundInProgress = true;

    // Place side bets on the Bet object if they were set before round started (only on hand 0)
    if (isPairBetPlaced || is21Plus3BetPlaced) {
        Bet bet = player.getBet(0);
        if (pairBetAmount > 0) {
            player.placePairBet(pairBetAmount, 0);
        }
        if (twentyOnePlusThreeBetAmount > 0) {
            player.placeTwentyOnePlusThreeBet(twentyOnePlusThreeBetAmount, 0);
        }
        
        // Update money display after side bets are placed
        if (listener != null) {
            listener.onMoneyChanged(player.getMoney());
        }
    }

    setBlockAccessToSideBets(false);

    if (listener != null) {
      listener.onRoundStart();
    }
    // TODO:
    // check if deck needs reshuffling AFTER onRoundStart
    // if (deck.getDecksRemaining() < numDecks * 0.25) {
    //   reshuffleDeck();
    // }

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

  public void onInitialDealComplete() {
    checkForBlackjacks(false);
  }

  // called by GUI after initial deal animation completes, or after insurance
  // decision
  public void checkForBlackjacks(boolean skipInsuranceCheck) {
    Hand playerHand = player.getHand(0);
    boolean playerBJ = playerHand.isBlackjack();

    // check if dealer's first card is an ace (unless we're resuming after
    // insurance)
    if (!skipInsuranceCheck && checkFirstCardAce()) {
      // insurance offer will be shown, return and wait for player response
      return;
    }

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

  public void onHitAnimationComplete(int handIndex) {
    Card card = drawCard();
    player.addCardToHand(card, handIndex);
    Hand hand = player.getHand(handIndex);

    // check if player busted
    if (hand.isBust()) {
      player.getBet(handIndex).setResult(Bet.BetResult.LOSE);
      handleHandCompletion(handIndex);
    }
    // Notify GUI to update display
    if (listener != null) {
      listener.onPlayerHandUpdated(player, handIndex);
    }
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

  public void onDoubleAnimationComplete(int handIndex) {
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

  public void onDealerRevealComplete() {
    if (dealerHiddenCard != null) {
      runningCount += dealerHiddenCard.getCountValue();
      dealerHiddenCard = null;
    }
    // Notify GUI it can start drawing dealer cards
    if (listener != null) {
      listener.onDealerDrawing(dealer);
    }
  }

  // dealer draws cards - called by GUI after reveal
  public void dealerDrawCards() {
    // GUI handles animation, checking mustHit() and calling dealerHit() for each
    // card
  }

  // called by GUI for each dealer card drawn
  public void dealerHit() {
    Card card = drawCard();
    dealer.addCard(card);
  }

  public void onDealerHitAnimationComplete() {
    Card card = drawCard();
    dealer.addCard(card);
    // GUI will check if dealer needs another card
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

      // handle insurance
      if (bet.isInsurancePlaced() && dealer.isBlackjack()) {
        // insurance pays 2:1
        player.addMoney(bet.getInsuranceBet());
      } else if (bet.isInsurancePlaced() && !dealer.isBlackjack()) {
        if (payout > 0) {
          player.addMoney(payout);
        }
      } else {
        if (payout > 0) {
          player.addMoney(payout);
        }
      }

      // handle Perfect Pair side bet (only for first hand)
      if (i == 0 && bet.isPairBetPlaced()) {
        Hand playerHand = player.getHand(0);
        // if split, combine first cards of both hands for pair evaluation
        if (player.getNumHands() >= 2) {
          Hand playerHand1 = player.getHand(1);
          Hand combinedHand = new Hand();
          combinedHand.addCard(playerHand.getCards().get(0));
          combinedHand.addCard(playerHand1.getCards().get(0));
          playerHand = combinedHand;

        }

        double pairPayout = DetermineSideBetPayout.calculatePairPayout(playerHand, bet.getCurrentPairBet());
        if (pairPayout > 0) {
          player.addMoney(pairPayout);
          if (listener != null) {
            listener.onSideBetWin(pairPayout);
          }
        }

      }

      // handle 21+3 side bet (only for first hand)
      if (i == 0 && bet.isTwentyOnePlusThreeBetPlaced()) {
        Hand playerHand = player.getHand(0);
        // if split, combine first cards of both hands for pair evaluation
        if (player.getNumHands() >= 2) {
          Hand playerHand1 = player.getHand(1);
          Hand combinedHand = new Hand();
          combinedHand.addCard(playerHand.getCards().get(0));
          combinedHand.addCard(playerHand1.getCards().get(0));
          playerHand = combinedHand;

        }
        // get dealer's first (visible) card
        Card dealerUpCard = dealer.getHand().getCards().get(0);
        double twentyOnePlusThreePayout = DetermineSideBetPayout.calculateTwentyOnePlusThreePayout(
            playerHand, dealerUpCard, bet.getCurrentTwentyOnePlusThreeBet());
        if (twentyOnePlusThreePayout > 0) {
          player.addMoney(twentyOnePlusThreePayout);
          if (listener != null) {
            listener.onSideBetWin(twentyOnePlusThreePayout);
          }
        }

      }
    }

    if (!areSideBetsRemembered) {
        isPairBetPlaced = false;
        is21Plus3BetPlaced = false;
        pairBetAmount = 0.0;
        twentyOnePlusThreeBetAmount = 0.0;
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

  public boolean checkFirstCardAce() {
    Hand dealerHand = dealer.getHand();
    boolean temp = dealerHand != null && dealerHand.isFirstCardAce();
    if (temp && !insuranceOffered && listener != null) {
      insuranceOffered = true;
      listener.onInsuranceOffer();
    }
    return temp;
  }

  public void acceptedInsurance(boolean bool) {
    if (bool && insuranceOffered && !insuranceResolved) {
      placeInsurance(0);
    }
    insuranceResolved = true;
    checkForBlackjacks(true); // skip insurance check, go straight to blackjack check
  }

  private void placeInsurance(int index) {
    Bet originalBet = player.getBet(index);
    double insuranceAmount = originalBet.getAmount() / 2;

    if (index == 0) {
      player.placeInsurance(insuranceAmount, index);
      // GUi updates money display
      if (listener != null) {
        listener.onMoneyChanged(player.getMoney());
      }
    }
  }

  public void onSideBetsPlaced(double pairAmount, double twentyOnePlusThreeAmount, int index,
        boolean areSideBetsRemembered) {

      // Only allow side bets on hand 0
      if (index != 0) {
          return;
      }
      // Just store the amounts - actual deduction happens in startRound
      this.pairBetAmount = pairAmount;
      this.twentyOnePlusThreeBetAmount = twentyOnePlusThreeAmount;
      this.isPairBetPlaced = pairAmount > 0;
      this.is21Plus3BetPlaced = twentyOnePlusThreeAmount > 0;

      setAreSideBetsRemembered(areSideBetsRemembered);
      setBlockAccessToSideBets(true);
  }

  public void setCustomBetAmount(int amount) {
    this.customBetAmount = amount;
  }

  private void setAreSideBetsRemembered(boolean remember) {
    this.areSideBetsRemembered = remember;
  }

  private void setBlockAccessToSideBets(boolean block) {
    this.blockAccessToSideBets = block;
  }

  // getters
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

  public boolean canContinuePlaying() {
    return player.getMoney() >= minimumBet;
  }

  public Card peekNextCard() {
    return deck.peek();
  }

  public int getCustomBetAmount() {
    return customBetAmount;
  }

  public double getPairBetAmount() {
    return pairBetAmount;
  }

  public double getTwentyOnePlusThreeBetAmount() {
    return twentyOnePlusThreeBetAmount;
  }

  public boolean isPairBetPlaced() {
    return isPairBetPlaced;
  }

  public boolean is21Plus3BetPlaced() {
    return is21Plus3BetPlaced;
  }

  public boolean areSideBetsRemembered() {
    return areSideBetsRemembered;
  }

  public boolean isBlockAccessToSideBets() {
    return blockAccessToSideBets;
  }
}
