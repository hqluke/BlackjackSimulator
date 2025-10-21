import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.animation.PauseTransition;
import java.util.List;

public class GUI extends Application implements GameStateListener {
    
    private Pane root;
    private Game game;
    private CardAnimation cardAnimation;
    private StackPane deck;
    private HBox dealerCards;
    private HBox playerCards;
    private Label playerValueLabel;
    private Label dealerValueLabel;
    private Label moneyLabel;
    private Label messageLabel;
    private Label betLabel;
    private Label countLabel;
    private TextField betField;
    private Button dealButton;
    private Button hitButton;
    private Button standButton;
    private Button doubleButton;
    private Button splitButton;
    private Button speedButton;
    
    private double WINDOW_WIDTH;
    private double WINDOW_HEIGHT;
    private double DEALER_Y;
    private double PLAYER_Y;

    private GUIEventListener gameEventListener;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        root.setStyle("-fx-background-color: darkgreen;");

        // show setup dialog
        SetupDialog.GameConfig config = SetupDialog.show();

        setFullscreenDimensions();

        initializeGame(primaryStage, config);
    }
    
    private void setFullscreenDimensions() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        WINDOW_WIDTH = screenBounds.getWidth();
        WINDOW_HEIGHT = screenBounds.getHeight();
        DEALER_Y = WINDOW_HEIGHT * 0.20;
        PLAYER_Y = WINDOW_HEIGHT * 0.50;
    }
    
    private void initializeGame(Stage primaryStage, SetupDialog.GameConfig config) {
        game = new Game(config.numDecks, config.startingMoney, config.minimumBet);
        game.setGameStateListener(this);
        gameEventListener = game;
        
        setupUI();

        cardAnimation.setAnimationSpeed(config.animationSpeed);
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(scene);
        
        if (config.isFullscreen) {
            primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");
            primaryStage.setFullScreen(true);
        }
        
        primaryStage.show();
        updateMoneyDisplay();
    }
    
    private void setupUI() {
        // money display
        moneyLabel = new Label("Money: $" + game.getPlayer().getMoney());
        moneyLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        moneyLabel.setLayoutX(20);
        moneyLabel.setLayoutY(20);
        root.getChildren().add(moneyLabel);

        // speed button
        speedButton = new Button("Change\nSpeed");
        speedButton.setPrefWidth(120);
        speedButton.setPrefHeight(70);
        speedButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 10px; -fx-font-weight: bold;");
        speedButton.setOnAction(e -> handleSpeedChange());
        speedButton.setLayoutX(WINDOW_WIDTH - 150);
        speedButton.setLayoutY(20);
        root.getChildren().add(speedButton);

        // running count
        countLabel = new Label("Count: " + game.getRunningCount() + " (" + String.format("%.2f", game.getTrueCount()) + ")");
        countLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        countLabel.setLayoutX(WINDOW_WIDTH - 200);
        countLabel.setLayoutY(100);
        root.getChildren().add(countLabel);

        // deck info display
        Label deckInfoLabel = new Label("Deck: " + game.getDeck().getNumDecks() + " decks, " + 
                                        game.getDeck().getCardsRemaining() + " cards");
        deckInfoLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        deckInfoLabel.setLayoutX(20);
        deckInfoLabel.setLayoutY(50);
        root.getChildren().add(deckInfoLabel);
        
        // message label
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: yellow; -fx-font-weight: bold;");
        messageLabel.setLayoutX(200);
        messageLabel.setLayoutY(WINDOW_HEIGHT - 150);
        messageLabel.setVisible(false);
        root.getChildren().add(messageLabel);

        // dealer section
        setupDealerArea();
        
        // player section
        setupPlayerArea();

        // betting section
        setupBettingArea();

        // action buttons
        setupActionButtons();
        
        // deck visual
        cardAnimation = new CardAnimation(root, (WINDOW_WIDTH + 400) / 2, (DEALER_Y - 150), this);
        deck = cardAnimation.createDeck();
        root.getChildren().add(deck);
    }
    
    private void setupDealerArea() {
        Label dealerLabel = new Label("Dealer");
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        dealerLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        dealerLabel.setLayoutY(DEALER_Y);
        root.getChildren().add(dealerLabel);
        
        dealerValueLabel = new Label("");
        dealerValueLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        dealerValueLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        dealerValueLabel.setLayoutY(DEALER_Y + 25);
        dealerValueLabel.setVisible(false);
        root.getChildren().add(dealerValueLabel);
        
        dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        dealerCards.setLayoutX((WINDOW_WIDTH - 400) / 2);
        dealerCards.setLayoutY(DEALER_Y + 50);
        root.getChildren().add(dealerCards);
    }
    
    private void setupPlayerArea() {
        Label playerLabel = new Label("Player");
        playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        playerLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        playerLabel.setLayoutY(PLAYER_Y);
        root.getChildren().add(playerLabel);
        
        playerValueLabel = new Label("");
        playerValueLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        playerValueLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        playerValueLabel.setLayoutY(PLAYER_Y + 25);
        playerValueLabel.setVisible(false);
        root.getChildren().add(playerValueLabel);
        
        playerCards = new HBox(10);
        playerCards.setAlignment(Pos.CENTER);
        playerCards.setLayoutX((WINDOW_WIDTH - 400) / 2);
        playerCards.setLayoutY(PLAYER_Y + 50);
        root.getChildren().add(playerCards);
    }
    
    private void setupBettingArea() {
        HBox mainBettingBox = new HBox(20);
        mainBettingBox.setAlignment(Pos.CENTER);
        mainBettingBox.setLayoutX(WINDOW_WIDTH / 2 - 180);
        mainBettingBox.setLayoutY(WINDOW_HEIGHT - 110);

        dealButton = new Button("Deal");
        dealButton.setPrefWidth(120);
        dealButton.setPrefHeight(40);
        dealButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        dealButton.setOnAction(e -> handleDeal());

        HBox bettingBox = new HBox(15);
        bettingBox.setAlignment(Pos.CENTER);

        betLabel = new Label("Place Bet:");
        betLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        int minBetInt = (int)game.getMinimumBet();
        betField = new TextField(String.valueOf(minBetInt));
        betField.setPrefWidth(150);
        betField.setPrefHeight(40);
        betField.setStyle("-fx-font-size: 16px;");
        
        betField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                betField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        bettingBox.getChildren().addAll(betLabel, betField);
        mainBettingBox.getChildren().addAll(dealButton, bettingBox);
        root.getChildren().add(mainBettingBox);
    }
    
    private void setupActionButtons() {
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setLayoutX(WINDOW_WIDTH / 2 - 180);
        buttonBar.setLayoutY(WINDOW_HEIGHT - 80);

        hitButton = new Button("Hit");
        standButton = new Button("Stand");
        doubleButton = new Button("Double");
        splitButton = new Button("Split");
        
        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 10px 20px;";
        hitButton.setStyle(buttonStyle);
        standButton.setStyle(buttonStyle);
        doubleButton.setStyle(buttonStyle);
        splitButton.setStyle(buttonStyle);
        
        hitButton.setOnAction(e -> {
            int handIndex = game.getPlayer().getCurrentHandIndex();
            Hand hand = game.getPlayer().getHand(handIndex);
            
            if (hand.isBust() || game.getPlayer().getBet(handIndex).getResult() != Bet.BetResult.PENDING) {
                return;
            }
            
            Card newCard = game.peekNextCard();
            double targetX;
            double targetY;
            if (game.getPlayer().getNumHands() > 1) {
                targetX = playerCards.getLayoutX() + (handIndex * 400) + (hand.getCards().size() * 55);
                targetY = playerCards.getLayoutY() + 40;
            } else {
                targetX = playerCards.getLayoutX() + (hand.getCards().size() * 117);
                targetY = playerCards.getLayoutY();
            }
            
            setActionButtonsDisabled(true);
            
            cardAnimation.dealSingleCard(
                getRankString(newCard),
                newCard.getSuit().getSymbol(),
                targetX,
                targetY,
                () -> {
                    if (gameEventListener != null) {
                        gameEventListener.onHitAnimationComplete(handIndex);
                    }
                    updateDisplay();
                    
                    if (game.isRoundInProgress()) {
                        Hand updatedHand = game.getPlayer().getHand(handIndex);
                        
                        // auto-stand if hand value is 21 or more
                        if (updatedHand.getValue() >= 21) {
                            game.stand(handIndex);
                        } else {
                            setActionButtonsDisabled(false);
                            updateActionButtons(game.getPlayer().getCurrentHandIndex());
                        }
                    } else {
                        setActionButtonsVisible(false);
                        showRoundResults(game.getPlayer().getBets());
                    }
                }
            );
        });
        standButton.setOnAction(e -> {
            int handIndex = game.getPlayer().getCurrentHandIndex();
            game.stand(handIndex);
            
            int nextHandIndex = handIndex + 1;
            if (nextHandIndex < game.getPlayer().getNumHands()) {
                updateDisplay();
                updateActionButtons(nextHandIndex);
            }
            // else: onDealerTurn will be called by game
        });
        doubleButton.setOnAction(e -> {
            int handIndex = game.getPlayer().getCurrentHandIndex();
            Hand hand = game.getPlayer().getHand(handIndex);
            
            if (hand.isBust() || game.getPlayer().getBet(handIndex).getResult() != Bet.BetResult.PENDING) {
                return;
            }
            
            game.doubleDown(handIndex); // this validates and doubles the bet
            
            Card newCard = game.peekNextCard();
            double targetX = playerCards.getLayoutX() + (hand.getCards().size() * 117);
            double targetY = playerCards.getLayoutY();
            
            setActionButtonsDisabled(true);
            
            // animate first
            cardAnimation.dealSingleCard(
                getRankString(newCard),
                newCard.getSuit().getSymbol(),
                targetX,
                targetY,
                () -> {
                    // after animation: actually add the card
                    if (gameEventListener != null) {
                        gameEventListener.onDoubleAnimationComplete(handIndex);
                    }
                    updateDisplay();
                    setActionButtonsDisabled(false);
                    
                    int nextHandIndex = handIndex + 1;
                    if (nextHandIndex < game.getPlayer().getNumHands()) {
                        updateActionButtons(nextHandIndex);
                    }
                    // else: onDealerTurn will be called
                }
            );
        });
        splitButton.setOnAction(e -> {
            int handIndex = game.getPlayer().getCurrentHandIndex();
            game.split(handIndex);
            updateDisplay();
            updateActionButtons(handIndex);
        });

        buttonBar.getChildren().addAll(hitButton, standButton, doubleButton, splitButton);
        root.getChildren().add(buttonBar);
        
        setActionButtonsVisible(false);
    }
    
    // event handlers
    private void handleDeal() {
        try {
            String betText = betField.getText();
            if (betText == null || betText.trim().isEmpty()) {
                showMessage("Please enter a bet amount!");
                return;
            }
            
            int betAmount = Integer.parseInt(betText);
            game.startRound(betAmount);
            game.setCustomBetAmount(betAmount);
            
        } catch (NumberFormatException ex) {
            showMessage("Invalid bet amount!");
        }
    }
    
    private void handleSpeedChange() {
        Integer newSpeed = SpeedDialog.show();
        if (newSpeed != null) {
            cardAnimation.setAnimationSpeed(newSpeed);
        }
    }
    
    // GameStateListener implementations
    @Override
    public void onRoundStart() {
        clearDisplay();
        betLabel.setVisible(false);
        dealButton.setVisible(false);
        betField.setVisible(false);
        messageLabel.setVisible(false);
    }
    
    @Override
    public void onInitialDeal(Player player, Dealer dealer) {
        Hand playerHand = player.getHand(0);
        Hand dealerHand = dealer.getHand();
        List<Card> playerCardsList = playerHand.getCards();
        List<Card> dealerCardsList = dealerHand.getCards();
        
        String p1Rank = getRankString(playerCardsList.get(0));
        String p1Suit = playerCardsList.get(0).getSuit().getSymbol();
        String p2Rank = getRankString(playerCardsList.get(1));
        String p2Suit = playerCardsList.get(1).getSuit().getSymbol();
        String d1Rank = getRankString(dealerCardsList.get(0));
        String d1Suit = dealerCardsList.get(0).getSuit().getSymbol();
        
        cardAnimation.dealInitialCards(
            p1Rank, p1Suit, d1Rank, d1Suit, p2Rank, p2Suit,
            playerCards.getLayoutX(), playerCards.getLayoutY(),
            dealerCards.getLayoutX(), dealerCards.getLayoutY(),
            () -> {
                // after animation: add static cards to display
                playerCards.getChildren().addAll(
                    createCard(p1Rank, p1Suit),
                    createCard(p2Rank, p2Suit)
                );
                
                dealerCards.getChildren().addAll(
                    createCard(d1Rank, d1Suit),
                    createHiddenCard()
                );
                
                playerValueLabel.setText("Value: " + playerHand.getValue());
                playerValueLabel.setVisible(true);
                updateMoneyDisplay();

                if (gameEventListener != null) {
                gameEventListener.onInitialDealComplete();
                }
            }
        );
    }
    
    @Override
    public void onPlayerTurn(Player player, int handIndex) {
        updateDisplay();
        // automatically stands if you get 21
        if (game.isRoundInProgress()) {
            Hand currentHand = player.getHand(handIndex);
            if (currentHand.getValue() >= 21) {
            game.stand(handIndex);

            return;
        }
            setActionButtonsVisible(true);
            setActionButtonsDisabled(false);
            updateActionButtons(handIndex);
        }
    }
    
    @Override
    public void onPlayerHandUpdated(Player player, int handIndex) {
        // just update the display - animation already happened
        updateDisplay();
        
        if (!game.isRoundInProgress()) {
            showRoundResults(player.getBets());
        } else {
            setActionButtonsVisible(true);
            updateActionButtons(handIndex);
        }
    }
    
    @Override
    public void onDealerTurn(Dealer dealer) {
        setActionButtonsVisible(false);
        
        // animate revealing dealer's hidden card
        game.startDealerPlay();
        dealerCards.getChildren().clear();
        for (Card card : dealer.getHand().getCards()) {
            dealerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
        }
        dealerValueLabel.setText("Value: " + dealer.getValue());
        dealerValueLabel.setVisible(true);
        
        // short pause, then start dealer drawing cards
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(cardAnimation.getAnimationSpeed() * 2));
        pause.setOnFinished(e -> {
            if (gameEventListener != null) {
                gameEventListener.onDealerRevealComplete();
            }
        });
        pause.play();
    }

    @Override
    public void onDealerDrawing(Dealer dealer) {
        // Called by game after dealer reveal is complete
        animateDealerDrawing(dealer);
    }

    private void animateDealerDrawing(Dealer dealer) {
        Hand dealerHand = dealer.getHand();

        if (dealer.mustHit() && !dealerHand.isBust()) {
            Card nextCard = game.peekNextCard();
            
            if (nextCard == null) {
                game.finalizeDealerPlay();
                return;
            }
            
            double targetX = dealerCards.getLayoutX() + (dealerHand.getCards().size() * 117);
            double targetY = dealerCards.getLayoutY();
            
            // animate the card
            cardAnimation.dealSingleCard(
                getRankString(nextCard),
                nextCard.getSuit().getSymbol(),
                targetX,
                targetY,
                () -> {
                    // after animation: actually draw the card
                    game.dealerHit();
                    
                    // add card to display
                    dealerCards.getChildren().add(createCard(getRankString(nextCard), nextCard.getSuit().getSymbol()));
                    dealerValueLabel.setText("Value: " + game.getDealer().getValue());
                    
                    // check if dealer needs more cards
                    if (dealer.mustHit() && !dealer.isBust()) {
                        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(cardAnimation.getAnimationSpeed()));
                        pause.setOnFinished(e -> animateDealerDrawing(dealer));
                        pause.play();
                    } else {
                        // dealer done
                        game.finalizeDealerPlay();
                    }
                }
            );
        } else {
            // dealer doesn't need to draw
            game.finalizeDealerPlay();
        }
    }
    
    @Override
    public void onRoundEnd(Player player, Dealer dealer, List<Bet> bets) {
        updateDisplay();
        showRoundResults(bets);
        setActionButtonsVisible(false);
        updateMoneyDisplay();
        
        betField.setText(String.valueOf((int)game.getCustomBetAmount()));
        
        if (!game.canContinuePlaying()) {
            showMessage("Game Over! Out of money.");
            dealButton.setDisable(true);
        } else {
            dealButton.setVisible(true);
            betField.setVisible(true);
            betLabel.setVisible(true);
        }
    }
    
    @Override
    public void onMoneyChanged(double newAmount) {
        updateMoneyDisplay();
    }
    
    @Override
    public void onDeckReshuffled(int numDecks, int cardsRemaining) {
        updateCountDisplay(); // update count display to show reset count
        showMessage("Deck reshuffled!");
        
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(2000));
        pause.setOnFinished(e -> {
            if (messageLabel.getText().equals("Deck reshuffled!")) {
                messageLabel.setVisible(false);
            }
        });
        pause.play();
    }
    
    @Override
    public void onError(String message) {
        showMessage(message);
    }
    
    // display update methods
    private void updateDisplay() {
        playerCards.getChildren().clear();
        
        int currentHandIndex = game.getPlayer().getCurrentHandIndex();
        int numHands = game.getPlayer().getNumHands();
        
        if (numHands > 1) {
            displaySplitHands(currentHandIndex, numHands);
        } else {
            displaySingleHand();
        }

        updateCountDisplay();
        updateDealerDisplay();
        updateMoneyDisplay();
    }

    private void updateCountDisplay() {
        // update the count display if implemented
        countLabel.setText("Count: " + game.getRunningCount() + " (" + String.format("%.2f", game.getTrueCount()) + ")");
    }

    private void displaySingleHand() {
        Hand playerHand = game.getPlayer().getHand(0);
        if (playerHand != null) {
            for (Card card : playerHand.getCards()) {
                playerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
            }
            playerValueLabel.setText("Value: " + playerHand.getValue());
            playerValueLabel.setVisible(true);
            
            Bet bet = game.getPlayer().getBet(0);
            if (bet != null && bet.getResult() != Bet.BetResult.PENDING) {
                displayHandResult(bet.getResult());
            }
        }
    }
    
    private void displaySplitHands(int currentHandIndex, int numHands) {
        for (int i = 0; i < numHands; i++) {
            Hand hand = game.getPlayer().getHand(i);
            if (hand != null) {
                VBox handBox = new VBox(5);
                handBox.setAlignment(Pos.CENTER);
                handBox.setStyle("-fx-padding: 0 30 0 30;");
                
                Label handLabel = new Label("Hand " + (i + 1) + (i == currentHandIndex ? " ◄ Current" : ""));
                handLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow; -fx-font-weight: bold;");
                
                HBox handCards = new HBox(5);
                handCards.setAlignment(Pos.CENTER);
                for (Card card : hand.getCards()) {
                    handCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
                }
                
                Label valueLabel = new Label("Value: " + hand.getValue());
                valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
                
                Bet bet = game.getPlayer().getBet(i);
                if (bet.getResult() != Bet.BetResult.PENDING) {
                    Label resultLabel = new Label(getResultText(bet.getResult()));
                    resultLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: lime; -fx-font-weight: bold;");
                    handBox.getChildren().addAll(handLabel, handCards, valueLabel, resultLabel);
                } else {
                    handBox.getChildren().addAll(handLabel, handCards, valueLabel);
                }
                
                playerCards.getChildren().add(handBox);
            }
        }
        playerValueLabel.setVisible(false);
    }
    
    private void updateDealerDisplay() {
        dealerCards.getChildren().clear();
        Hand dealerHand = game.getDealer().getHand();
        List<Card> dealerCardsList = dealerHand.getCards();
        
        for (int i = 0; i < dealerCardsList.size(); i++) {
            if (i == 1 && dealerHand.isHideSecondCard()) {
                dealerCards.getChildren().add(createHiddenCard());
            } else {
                Card card = dealerCardsList.get(i);
                dealerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
            }
        }
        
        if (!dealerHand.isHideSecondCard()) {
            dealerValueLabel.setText("Value: " + dealerHand.getValue());
            dealerValueLabel.setVisible(true);
        }
    }
    
    private void displayHandResult(Bet.BetResult result) {
        Label resultLabel = new Label(getResultText(result));
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: lime; -fx-font-weight: bold;");
        resultLabel.setLayoutX((WINDOW_WIDTH - 100) / 2);
        resultLabel.setLayoutY(PLAYER_Y + 300);
        root.getChildren().add(resultLabel);
    }
    
    private void updateActionButtons(int handIndex) {
        Hand hand = game.getPlayer().getHand(handIndex);
        Bet bet = game.getPlayer().getBet(handIndex);
        
        if (hand.isBust() || bet.getResult() != Bet.BetResult.PENDING) {
            hitButton.setDisable(true);
            standButton.setDisable(false);
            doubleButton.setDisable(true);
            splitButton.setDisable(true);
            return;
        }
        
        // disable hit if hand is over 21 or blackjack
        hitButton.setDisable(hand.getValue() >= 21);
        standButton.setDisable(false);
        
        boolean isSplitHand = game.getPlayer().getNumHands() > 1;
        double betAmount = game.getPlayer().getBet(0).getAmount();
        
        doubleButton.setDisable(isSplitHand || !hand.canDouble() || !game.getPlayer().canAfford(betAmount));
        splitButton.setDisable(!hand.canSplit() || !game.getPlayer().canAfford(betAmount));
    }
    
    private void setActionButtonsVisible(boolean visible) {
        hitButton.setVisible(visible);
        standButton.setVisible(visible);
        doubleButton.setVisible(visible);
        splitButton.setVisible(visible);
    }
    
    private void setActionButtonsDisabled(boolean disabled) {
        hitButton.setDisable(disabled);
        standButton.setDisable(disabled);
        doubleButton.setDisable(disabled);
        splitButton.setDisable(disabled);
    }
    
    private void clearDisplay() {
        playerCards.getChildren().clear();
        dealerCards.getChildren().clear();
        
        root.getChildren().removeIf(node -> 
            node instanceof Label && ((Label)node).getText().matches("WIN!|BUST|PUSH|BLACKJACK!")
        );
        
        playerValueLabel.setVisible(false);
        dealerValueLabel.setVisible(false);
        messageLabel.setVisible(false);
    }
    
    private void showRoundResults(List<Bet> bets) {
        StringBuilder msg = new StringBuilder();
        double totalWinnings = 0;
        
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            if (bets.size() > 1) {
                msg.append("Hand ").append(i + 1).append(": ");
            }
            
            switch (bet.getResult()) {
                case WIN:
                    double winAmount = bet.getAmount();
                    totalWinnings += winAmount;
                    msg.append("Win! +$").append(winAmount).append(" \n");
                    break;
                case LOSE:
                    msg.append("Lose -$").append(bet.getAmount()).append(" \n");
                    break;
                case PUSH:
                    msg.append("Push ").append(" \n");
                    break;
                case BLACKJACK:
                    double bjAmount = bet.getAmount() * 1.5;
                    totalWinnings += bjAmount;
                    msg.append("BLACKJACK! +$").append(bjAmount).append(" \n");
                    break;
                case PENDING:
                    break;
            }
        }
        
        if (bets.size() > 1 && totalWinnings > 0) {
            msg.append("Total: +$").append(totalWinnings);
        }
        
        showMessage(msg.toString());
    }
    
    private String getResultText(Bet.BetResult result) {
        switch (result) {
            case WIN: return "WIN!";
            case LOSE: return "BUST";
            case PUSH: return "PUSH";
            case BLACKJACK: return "BLACKJACK!";
            default: return "";
        }
    }
    
    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
    }
    
    private void updateMoneyDisplay() {
        moneyLabel.setText(String.format("Money: $%.2f", game.getPlayer().getMoney()));
    }
    
    // card creation methods
    private String getRankString(Card card) {
        switch (card.getRank()) {
            case ACE: return "A";
            case JACK: return "J";
            case QUEEN: return "Q";
            case KING: return "K";
            default: return String.valueOf(card.getRank().getValue());
        }
    }

    @Override
    public void onInsuranceOffer() {
    InsuranceDialog.show(
        () -> {
            game.acceptedInsurance(true); 
        },
        () -> {
            game.acceptedInsurance(false); 
        }
    );
    }

    public StackPane createCard(String rank, String suit) {
        StackPane card = new StackPane();
        
        Rectangle cardBg = new Rectangle(105, 150);
        cardBg.setFill(Color.WHITE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.CENTER);
        
        Label rankLabel = new Label(rank);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        
        Label suitLabel = new Label(suit);
        suitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        
        Color textColor = (suit.equals("♥") || suit.equals("♦")) ? Color.RED : Color.BLACK;
        rankLabel.setTextFill(textColor);
        suitLabel.setTextFill(textColor);
        
        cardContent.getChildren().addAll(rankLabel, suitLabel);
        card.getChildren().addAll(cardBg, cardContent);
        return card;
    }
    
    public StackPane createHiddenCard() {
        StackPane card = new StackPane();
        
        Rectangle cardBg = new Rectangle(105, 150);
        cardBg.setFill(Color.DARKBLUE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        
        Label hiddenLabel = new Label("?");
        hiddenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 54));
        hiddenLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(cardBg, hiddenLabel);
        return card;
    }
    
    public StackPane createDeckCard() {
        StackPane card = new StackPane();
        Rectangle cardBg = new Rectangle(105, 150);
        cardBg.setFill(Color.DARKBLUE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        card.getChildren().add(cardBg);
        return card;
    }
}