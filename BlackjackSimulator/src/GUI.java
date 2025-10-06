import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GUI extends Application {
    
    private Pane root;
    private CardAnimation cardAnimation;
    private StackPane deck;
    private HBox dealerCards;
    private HBox playerCards;
    private Label playerValueLabel;
    
    // absolute positioning constants for layout
    // I might have to change back to VBox for the window if there isn't a full screen event listener
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;
    private static final double DEALER_Y = 80;
    private static final double PLAYER_Y = 320;

    @Override
    public void start(Stage primaryStage) {
        // single pane absolute positioning
        root = new Pane();
        root.setStyle("-fx-background-color: darkgreen;");

        // dealer label
        Label dealerLabel = new Label("Dealer");
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        dealerLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        dealerLabel.setLayoutY(DEALER_Y);
        root.getChildren().add(dealerLabel);
        
        // dealer cards container
        dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        dealerCards.setLayoutX((WINDOW_WIDTH - 160) / 2); // 2 cards (70px each) + spacing (10px) = 150px
        dealerCards.setLayoutY(DEALER_Y + 30);
        root.getChildren().add(dealerCards);

        // player label
        Label playerLabel = new Label("Player");
        playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        playerLabel.setLayoutX((WINDOW_WIDTH - 60) / 2);
        playerLabel.setLayoutY(PLAYER_Y);
        root.getChildren().add(playerLabel);
        
        // player cards container
        playerCards = new HBox(10);
        playerCards.setAlignment(Pos.CENTER);
        playerCards.setLayoutX((WINDOW_WIDTH - 160) / 2);
        playerCards.setLayoutY(PLAYER_Y + 30);
        root.getChildren().add(playerCards);
        
        // player value label
        playerValueLabel = new Label("Value: 21 (Blackjack!)");
        playerValueLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: yellow; -fx-font-weight: bold;");
        playerValueLabel.setLayoutX((WINDOW_WIDTH - 200) / 2);
        playerValueLabel.setLayoutY(PLAYER_Y + 150);
        playerValueLabel.setVisible(false);
        root.getChildren().add(playerValueLabel);

        // buttons
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setLayoutX(225);
        buttonBar.setLayoutY(WINDOW_HEIGHT - 80);

        Button hitButton = new Button("Hit");
        Button standButton = new Button("Stand");
        Button doubleButton = new Button("Double");
        Button splitButton = new Button("Split");
        
        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 10px 20px;";
        String transparentButtonStyle = buttonStyle + "-fx-opacity: 0.5;";
        
        hitButton.setStyle(buttonStyle);
        standButton.setStyle(buttonStyle);
        doubleButton.setStyle(transparentButtonStyle);
        splitButton.setStyle(transparentButtonStyle);

        buttonBar.getChildren().addAll(hitButton, standButton, doubleButton, splitButton);
        root.getChildren().add(buttonBar);

        cardAnimation = new CardAnimation(root, 50, 50, this);
        
        // deck visual
        deck = cardAnimation.createDeck();
        root.getChildren().add(deck);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // dealing animation
        javafx.application.Platform.runLater(() -> {
            dealInitialCards();
        });
    }
    
    private void dealInitialCards() {
    	// get card positions
        double dealerCardsX = dealerCards.getLayoutX();
        double dealerCardsY = dealerCards.getLayoutY();
        double playerCardsX = playerCards.getLayoutX();
        double playerCardsY = playerCards.getLayoutY();
        
        // animate cards to these positions
        cardAnimation.dealInitialCards(
            "A", "♥",      // player card 1
            "K", "♠",      // dealer card 1
            "10", "♦",     // player card 2
            playerCardsX, playerCardsY,  // player position
            dealerCardsX, dealerCardsY,  // dealer position
            () -> {
                // add static cards after animation
                playerCards.getChildren().addAll(
                    createCard("A", "♥"),
                    createCard("10", "♦")
                );
                
                dealerCards.getChildren().addAll(
                    createCard("K", "♠"),
                    createHiddenCard()
                );
                
                playerValueLabel.setVisible(true);
            }
        );
    }

    // card creator
    public StackPane createCard(String rank, String suit) {
        StackPane card = new StackPane();
        
        Rectangle cardBg = new Rectangle(70, 100);
        cardBg.setFill(Color.WHITE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.CENTER);
        
        Label rankLabel = new Label(rank);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label suitLabel = new Label(suit);
        suitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Color textColor = (suit.equals("♥") || suit.equals("♦")) ? Color.RED : Color.BLACK;
        rankLabel.setTextFill(textColor);
        suitLabel.setTextFill(textColor);
        
        cardContent.getChildren().addAll(rankLabel, suitLabel);
        card.getChildren().addAll(cardBg, cardContent);
        return card;
    }
    
    // dealers hidden card
    public StackPane createHiddenCard() {
        StackPane card = new StackPane();
        
        Rectangle cardBg = new Rectangle(70, 100);
        cardBg.setFill(Color.DARKBLUE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        
        Label hiddenLabel = new Label("?");
        hiddenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        hiddenLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(cardBg, hiddenLabel);
        return card;
    }
    
    // top left card (where the animation comes from)
    public StackPane createDeckCard() {
        StackPane card = new StackPane();
        Rectangle cardBg = new Rectangle(70, 100);
        cardBg.setFill(Color.DARKBLUE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        card.getChildren().add(cardBg);
        return card;
    }

}