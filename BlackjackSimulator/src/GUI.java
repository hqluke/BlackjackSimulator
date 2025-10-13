import javafx.application.Application;
import javafx.application.Platform;
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
import javax.swing.*;
import java.util.Optional;

public class GUI extends Application {
    
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
    // private boolean isFullscreen = false;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        root.setStyle("-fx-background-color: darkgreen;");

        // initialize game with setup dialog
        showSetupDialog(primaryStage);
    }
    
    private void showSetupDialog(Stage primaryStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Blackjack Setup");
        dialog.setHeaderText("Configure your game");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField decksField = new TextField("6");
        TextField moneyField = new TextField("1000");
        TextField minBetField = new TextField("10");
        
        grid.add(new Label("Number of Decks (min 1):"), 0, 0);
        grid.add(decksField, 1, 0);
        grid.add(new Label("Starting Money:"), 0, 1);
        grid.add(moneyField, 1, 1);
        grid.add(new Label("Minimum Bet:"), 0, 2);
        grid.add(minBetField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        
        dialog.showAndWait();
        
        try {
            int numDecks = Integer.parseInt(decksField.getText());
            int startingMoney = Integer.parseInt(moneyField.getText());
            int minBet = Integer.parseInt(minBetField.getText());
            
            // validate minimum 1 deck
            if (numDecks < 1) {
                numDecks = 1;
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Deck Count");
                alert.setHeaderText(null);
                alert.setContentText("Minimum 1 deck required. Setting to 1 deck.");
                alert.showAndWait();
            }
            
            // always use fullscreen
            setFullscreenDimensions();
            
            System.out.println("Setup: decks=" + numDecks + " money=" + startingMoney + " minBet=" + minBet);
            
            initializeGame(primaryStage, numDecks, startingMoney, minBet);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid setup values, using defaults");
            initializeGame(primaryStage, 6, 1000, 10);
        }
    }
    
    private void setFullscreenDimensions() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        WINDOW_WIDTH = screenBounds.getWidth();
        WINDOW_HEIGHT = screenBounds.getHeight();
        
        // adjust dealer and player Y positions for better centering
        DEALER_Y = WINDOW_HEIGHT * 0.20;
        PLAYER_Y = WINDOW_HEIGHT * 0.50;
    }
    
    private void initializeGame(Stage primaryStage, int numDecks, int startingMoney, int minBet) {
        System.out.println("Initializing game...");
        game = new Game(this, numDecks, startingMoney, minBet);
        System.out.println("Game created");
        
        setupUI();
        System.out.println("UI setup complete");
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(scene);
        
        // set fullscreen properties before showing
        primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.valueOf("ESC"));
        
        primaryStage.show();
        
        // set fullscreen after showing the stage
        primaryStage.setFullScreen(true);
        
        updateMoneyDisplay();
        System.out.println("Game initialization complete");
    }
    
    private void setupUI() {
        // money display
        moneyLabel = new Label("Money: $1000.00");
        moneyLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        moneyLabel.setLayoutX(20);
        moneyLabel.setLayoutY(20);
        root.getChildren().add(moneyLabel);

        // speed button
        speedButton = new Button("Change\nSpeed");
        speedButton.setPrefWidth(120);
        speedButton.setPrefHeight(70);
        speedButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 10px; -fx-font-weight: bold; -fx-text-alignment: center;");
        speedButton.setOnAction(e -> {
            System.out.println("=== Speed button clicked! ===");
            toggleSpeedOptions();
        });
        speedButton.setLayoutX(WINDOW_WIDTH - 150);
        speedButton.setLayoutY(20);
        root.getChildren().add(speedButton);
        

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
        messageLabel.setLayoutX(WINDOW_WIDTH - WINDOW_WIDTH + 200);
        messageLabel.setLayoutY(WINDOW_HEIGHT - 150);
        messageLabel.setVisible(false);
        root.getChildren().add(messageLabel);

        // dealer section
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

        // player section
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

        // betting section
        VBox verticalBettingBox = new VBox(10);
        verticalBettingBox.setAlignment(Pos.CENTER);
        verticalBettingBox.setLayoutX(WINDOW_WIDTH / 2 - 200);
        verticalBettingBox.setLayoutY(WINDOW_HEIGHT - 120);
        root.getChildren().add(verticalBettingBox);

        HBox bettingBox = new HBox(15);
        bettingBox.setAlignment(Pos.CENTER);
        bettingBox.setLayoutX(WINDOW_WIDTH / 2);
        bettingBox.setLayoutY(WINDOW_HEIGHT - 120);

        betLabel = new Label("Place Bet:");
        betLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        int minBetInt = (int)game.getMinimumBet();
        betField = new TextField(String.valueOf(minBetInt));
        betField.setPrefWidth(150);
        betField.setPrefHeight(40);
        betField.setStyle("-fx-font-size: 16px;");
        
        // only allow integer input
        betField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                betField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        dealButton = new Button("Deal");
        dealButton.setPrefWidth(120);
        dealButton.setPrefHeight(40);
        dealButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 30px; -fx-font-weight: bold; ");
        dealButton.setOnAction(e -> {
            System.out.println("=== Deal button clicked! ===");
            handleDeal();
        });
        
        System.out.println("Deal button created and event handler attached");
        
        bettingBox.getChildren().addAll(betLabel, betField);
        
        verticalBettingBox.getChildren().add(dealButton);
        root.getChildren().add(bettingBox);

        // action buttons
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
        
        hitButton.setOnAction(e -> handleHit());
        standButton.setOnAction(e -> handleStand());
        doubleButton.setOnAction(e -> handleDouble());
        splitButton.setOnAction(e -> handleSplit());

        buttonBar.getChildren().addAll(hitButton, standButton, doubleButton, splitButton);
        root.getChildren().add(buttonBar);
        
        setActionButtonsVisible(false);

        // deck visual
        cardAnimation = new CardAnimation(root, (WINDOW_WIDTH + 400) / 2, (DEALER_Y - 150), this);
        deck = cardAnimation.createDeck();
        root.getChildren().add(deck);
    }
    
    private void handleDeal() {
        System.out.println("=== handleDeal called ===");
        try {
            String betText = betField.getText();
            System.out.println("Bet text: '" + betText + "'");
            
            if (betText == null || betText.trim().isEmpty()) {
                System.out.println("Bet text is empty or null");
                showMessage("Please enter a bet amount!");
                return;
            }
            
            int betAmount = Integer.parseInt(betText);
            System.out.println("Parsed bet amount: " + betAmount);
            
            // clear previous round UI
            clearDisplay();
            
            game.startRound(betAmount);
            System.out.println("Round started successfully");

            betLabel.setVisible(false);
            dealButton.setVisible(false);
            betField.setVisible(false);
            messageLabel.setVisible(false);
            
            System.out.println("About to call animateDeal()");
            animateDeal();
            System.out.println("animateDeal() called");
            
        } catch (NumberFormatException ex) {
            System.out.println("Number format exception: " + ex.getMessage());
            ex.printStackTrace();
            showMessage("Invalid bet amount!");
        } catch (Exception ex) {
            System.out.println("Exception in handleDeal: " + ex.getMessage());
            ex.printStackTrace();
            showMessage(ex.getMessage());
        }
    }
    
    private void animateDeal() {
        Hand playerHand = game.getPlayer().getHand(0);
        Hand dealerHand = game.getDealer().getHand();
        List<Card> playerCardsList = playerHand.getCards();
        List<Card> dealerCardsList = dealerHand.getCards();
        
        double dealerCardsX = dealerCards.getLayoutX();
        double dealerCardsY = dealerCards.getLayoutY();
        double playerCardsX = playerCards.getLayoutX();
        double playerCardsY = playerCards.getLayoutY();
        
        String p1Rank = getRankString(playerCardsList.get(0));
        String p1Suit = playerCardsList.get(0).getSuit().getSymbol();
        String p2Rank = getRankString(playerCardsList.get(1));
        String p2Suit = playerCardsList.get(1).getSuit().getSymbol();
        
        String d1Rank = getRankString(dealerCardsList.get(0));
        String d1Suit = dealerCardsList.get(0).getSuit().getSymbol();
        
        cardAnimation.dealInitialCards(
            p1Rank, p1Suit,
            d1Rank, d1Suit,
            p2Rank, p2Suit,
            playerCardsX, playerCardsY,
            dealerCardsX, dealerCardsY,
            () -> {
                // add static cards to display after animation
                playerCards.getChildren().addAll(
                    createCard(p1Rank, p1Suit),
                    createCard(p2Rank, p2Suit)
                );
                
                dealerCards.getChildren().addAll(
                    createCard(d1Rank, d1Suit),
                    createHiddenCard()
                );
                
                // update labels
                playerValueLabel.setText("Value: " + playerHand.getValue());
                playerValueLabel.setVisible(true);
                
                updateMoneyDisplay();
                
                if (!game.isRoundInProgress()) {
                    if(dealerHand.isFirstCardAce() && !playerHand.isBlackjack()) {
                        showMessage("Dealer has an Ace! Asking for Insurance...");
                        Platform.runLater(() -> {
                            createInsurancePopUp(null);
                            
                        
                        });
                    }

                                        // blackjack detected - reveal dealer and end
                    dealerCards.getChildren().clear();
                    for (Card card : dealerCardsList) {
                        dealerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
                    }
                    dealerValueLabel.setText("Value: " + dealerHand.getValue());
                    dealerValueLabel.setVisible(true);
                    endRound();

                } else {
                    setActionButtonsVisible(true);
                    updateActionButtons();
                }
            }
        );
    }

    private void createInsurancePopUp(Stage primStage) {
        // Create a pop-up dialog for insurance
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Insurance");
        dialog.setHeaderText("Dealer has an Ace! Would you like to place an insurance bet?");
        
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().setAll(yesButton, noButton);
        

        // Show the dialog and wait for a response
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Player chose to take insurance
             game.placeInsurance();
        }

    }
    
    private void handleHit() {
        int handIndex = game.getPlayer().getCurrentHandIndex();
        Hand hand = game.getPlayer().getHand(handIndex);
        
        // check if hand is already resolved
        if (hand.isBust() || game.getPlayer().getBet(handIndex).getResult() != Bet.BetResult.PENDING) {
            return; // don't allow hitting on resolved hands
        }
        
        // get card positions for animation
        double targetX = playerCards.getLayoutX();
        double targetY = playerCards.getLayoutY();
        
        // get the new card before adding it
        Card newCard = game.peekNextCard();
        
        // disable buttons during animation
        setActionButtonsDisabled(true);
        
        // animate the new card
        cardAnimation.dealSingleCard(
            getRankString(newCard),
            newCard.getSuit().getSymbol(),
            targetX + (hand.getCards().size() * 117),
            targetY,
            () -> {
                // after animation, actually add the card to the hand
                game.hit(handIndex);
                updateDisplay();
                
                // re-enable buttons
                setActionButtonsDisabled(false);
                
                if (!game.isRoundInProgress()) {
                    endRound();
                } else {
                    updateActionButtons();
                }
            }
        );
    }
    
    private void handleStand() {
        int handIndex = game.getPlayer().getCurrentHandIndex();
        game.stand(handIndex);
        
        // check if we moved to another hand (split scenario)
        int nextHandIndex = handIndex + 1;
        if (nextHandIndex < game.getPlayer().getNumHands()) {
            // if we moved to another hand, update the buttons
            updateDisplay();
            updateActionButtons();
        } else {
            // all player hands are done, disable buttons during dealer's turn
            setActionButtonsVisible(false);
            
            // reveal dealer's hidden card first
            animateDealerReveal(() -> {
                // then animate dealer drawing cards if needed
                animateDealerPlay();
            });
        }
    }
    
    private void handleDouble() {
        int handIndex = game.getPlayer().getCurrentHandIndex();
        Hand hand = game.getPlayer().getHand(handIndex);
        
        // check if hand is already resolved
        if (hand.isBust() || game.getPlayer().getBet(handIndex).getResult() != Bet.BetResult.PENDING) {
            return;
        }
        
        try {
            // get card positions for animation
            double targetX = playerCards.getLayoutX();
            double targetY = playerCards.getLayoutY();
            
            // get the new card before adding it
            Card newCard = game.peekNextCard();
            
            // disable buttons during animation
            setActionButtonsDisabled(true);
            
            // animate the new card
            cardAnimation.dealSingleCard(
                getRankString(newCard),
                newCard.getSuit().getSymbol(),
                targetX + (hand.getCards().size() * 117),
                targetY,
                () -> {
                    // after animation, perform the double down
                    game.doubleDown(handIndex);
                    updateDisplay();
                    
                    // re-enable buttons
                    setActionButtonsDisabled(false);
                    
                    if (!game.isRoundInProgress()) {
                        endRound();
                    } else {
                        updateActionButtons();
                    }
                }
            );
        } catch (Exception ex) {
            showMessage(ex.getMessage());
            setActionButtonsDisabled(false);
        }
    }
    
    private void handleSplit() {
        int handIndex = game.getPlayer().getCurrentHandIndex();
        try {
            game.split(handIndex);
            updateDisplay();
            updateActionButtons();
        } catch (Exception ex) {
            showMessage(ex.getMessage());
        }
    }
    
    private void setActionButtonsDisabled(boolean disabled) {
        hitButton.setDisable(disabled);
        standButton.setDisable(disabled);
        doubleButton.setDisable(disabled);
        splitButton.setDisable(disabled);
    }
    
    private void updateDisplay() {
        // update player cards - show all hands if split
        playerCards.getChildren().clear();
        
        int currentHandIndex = game.getPlayer().getCurrentHandIndex();
        int numHands = game.getPlayer().getNumHands();
        
        if (numHands > 1) {
            // split hands - show all hands with spacing
            for (int i = 0; i < numHands; i++) {
                Hand hand = game.getPlayer().getHand(i);
                if (hand != null) {
                    // create container for this hand
                    VBox handBox = new VBox(5);
                    handBox.setAlignment(Pos.CENTER);
                    handBox.setStyle("-fx-padding: 0 30 0 30;"); // add horizontal padding between hands
                    
                    // label to show which hand
                    Label handLabel = new Label("Hand " + (i + 1) + (i == currentHandIndex ? " ◄ Current" : ""));
                    handLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow; -fx-font-weight: bold;");
                    
                    // cards for this hand
                    HBox handCards = new HBox(5);
                    handCards.setAlignment(Pos.CENTER);
                    for (Card card : hand.getCards()) {
                        handCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
                    }
                    
                    // value label
                    Label valueLabel = new Label("Value: " + hand.getValue());
                    valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
                    
                    // result label if hand is finished
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
        } else {
            // single hand - normal display
            Hand playerHand = game.getPlayer().getHand(0);
            if (playerHand != null) {
                for (Card card : playerHand.getCards()) {
                    playerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
                }
                playerValueLabel.setText("Value: " + playerHand.getValue());
                playerValueLabel.setVisible(true);
                
                // show result if hand is finished
                Bet bet = game.getPlayer().getBet(0);
                if (bet != null && bet.getResult() != Bet.BetResult.PENDING) {
                    Label resultLabel = new Label(getResultText(bet.getResult()));
                    resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: lime; -fx-font-weight: bold;");
                    resultLabel.setLayoutX((WINDOW_WIDTH - 100) / 2);
                    resultLabel.setLayoutY(PLAYER_Y + 300); // below the cards
                    root.getChildren().add(resultLabel);
                }
            }
        }
        
        // update dealer cards
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
        
        updateMoneyDisplay();
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
    
    private void updateActionButtons() {
        int currentHandIndex = game.getPlayer().getCurrentHandIndex();
        Hand hand = game.getPlayer().getHand(currentHandIndex);
        Bet bet = game.getPlayer().getBet(currentHandIndex);
        
        // if current hand is already resolved, disable hit and enable stand only
        if (hand.isBust() || bet.getResult() != Bet.BetResult.PENDING) {
            hitButton.setDisable(true);
            standButton.setDisable(false);
            doubleButton.setDisable(true);
            splitButton.setDisable(true);
            return;
        }
        
        hitButton.setDisable(false);
        standButton.setDisable(false);
        
        // disable double if hand is split (more than 1 hand exists)
        boolean isSplitHand = game.getPlayer().getNumHands() > 1;
        doubleButton.setDisable(isSplitHand || !hand.canDouble() || !game.getPlayer().canAfford(game.getPlayer().getBet(0).getAmount()));
        hitButton.setDisable(hand.isBlackjack());
        
        splitButton.setDisable(!hand.canSplit() || !game.getPlayer().canAfford(game.getPlayer().getBet(0).getAmount()));
    }
    
    private void setActionButtonsVisible(boolean visible) {
        hitButton.setVisible(visible);
        standButton.setVisible(visible);
        doubleButton.setVisible(visible);
        splitButton.setVisible(visible);
    }
    

    private void clearDisplay() {
        // clear all cards from display
        playerCards.getChildren().clear();
        dealerCards.getChildren().clear();
        
        // remove any result labels that were added to root
        root.getChildren().removeIf(node -> 
            node instanceof Label && ((Label)node).getText().matches("WIN!|BUST|PUSH|BLACKJACK!")
        );
        
        // hide value labels
        playerValueLabel.setVisible(false);
        dealerValueLabel.setVisible(false);
        
        // hide message
        messageLabel.setVisible(false);
    }
    
    private void animateDealerReveal(Runnable onComplete) {
        // reveal the hidden card
        game.getDealer().revealCards();
        
        // manually update dealer display without calling updateDisplay()
        dealerCards.getChildren().clear();
        Hand dealerHand = game.getDealer().getHand();
        for (Card card : dealerHand.getCards()) {
            dealerCards.getChildren().add(createCard(getRankString(card), card.getSuit().getSymbol()));
        }
        
        // show dealer value
        dealerValueLabel.setText("Value: " + dealerHand.getValue());
        dealerValueLabel.setVisible(true);
        
        // short pause before dealer draws
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(cardAnimation.getAnimationSpeed() * 2));
        pause.setOnFinished(e -> onComplete.run());
        pause.play();
    }
    

    private void animateDealerPlay() {
        Hand dealerHand = game.getDealer().getHand();
        
        // check if dealer needs to draw more cards
        if (game.getDealer().mustHit() && !dealerHand.isBust()) {
            // get the next card without removing it yet
            Card nextCard = game.peekNextCard();
            
            if (nextCard == null) {
                // deck is empty somehow, finalize the round
                game.finalizeRound();
                endRound();
                return;
            }
            
            // calculate position for next dealer card
            double targetX = dealerCards.getLayoutX() + (dealerHand.getCards().size() * 117);
            double targetY = dealerCards.getLayoutY();
            
            // animate the card
            cardAnimation.dealSingleCard(
                getRankString(nextCard),
                nextCard.getSuit().getSymbol(),
                targetX,
                targetY,
                () -> {
                    // after animation completes, actually draw the card and add to dealer
                    game.dealerHit(); // this properly draws from deck and adds to dealer
                    
                    // manually add the card to display
                    dealerCards.getChildren().add(createCard(getRankString(nextCard), nextCard.getSuit().getSymbol()));
                    
                    // update dealer value label
                    dealerValueLabel.setText("Value: " + game.getDealer().getValue());
                    
                    // recursively continue if dealer needs more cards
                    if (game.getDealer().mustHit() && !game.getDealer().isBust()) {
                        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(cardAnimation.getAnimationSpeed()));
                        pause.setOnFinished(e -> animateDealerPlay());
                        pause.play();
                    } else {
                        // dealer is done, finalize and end the round
                        game.finalizeRound();
                        endRound();
                    }
                }
            );
        } else {
            // dealer doesn't need to draw, finalize and end the round
            game.finalizeRound();
            endRound();
        }
    }
    
    private void endRound() {
        setActionButtonsVisible(false);
        updateDisplay();
        
        String resultMessage = calculateResultMessage();
        showMessage(resultMessage);
        
        updateMoneyDisplay();
        
        // reset bet field to minimum bet
        betField.setText(String.valueOf((int)game.getMinimumBet()));
        
        if (!game.canContinuePlaying()) {
            showMessage("Game Over! Out of money.");
            dealButton.setDisable(true);
        } else {
            dealButton.setVisible(true);
            betField.setVisible(true);
            betLabel.setVisible(true);
        }
    }
    
    private String calculateResultMessage() {
        StringBuilder msg = new StringBuilder();
        List<Bet> bets = game.getPlayer().getBets();
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
                    // should not happen, but handle it just in case
                    break;
            }
        }
        
        if (bets.size() > 1 && totalWinnings > 0) {
            msg.append("Total: +$").append(totalWinnings);
        }
        
        return msg.toString();
    }
    
    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
    }
    

    
    private void updateMoneyDisplay() {
        moneyLabel.setText(String.format("Money: $%.2f", game.getPlayer().getMoney()));
    }
    
    private String getRankString(Card card) {
        switch (card.getRank()) {
            case ACE: return "A";
            case JACK: return "J";
            case QUEEN: return "Q";
            case KING: return "K";
            default: return String.valueOf(card.getRank().getValue());
        }
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

    public void setAnimationSpeed(int speedMilliseconds) {
        cardAnimation.setAnimationSpeed(speedMilliseconds);
    }

    public void toggleSpeedOptions() {
        String[] options = {"Default", "2x Speed", "4x Speed", "8x Speed", "12x Speed"};
        String selectedOption = (String) JOptionPane.showInputDialog(
            null,
            "Select Animation Speed:",
            "Speed Options",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selectedOption != null) {
            switch (selectedOption) {
                case "Default":
                    setAnimationSpeed(200);
                    break;
                case "2x Speed":
                    setAnimationSpeed(100);
                    break;
                case "4x Speed":
                    setAnimationSpeed(50);
                    break;
                case "8x Speed":
                    setAnimationSpeed(25);
                    break;
                case "12x Speed":
                    setAnimationSpeed(15);
                    break;
                default:
                    setAnimationSpeed(200);
            }
            System.out.println("Animation speed set to: " + selectedOption);
        }
    }
}