import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class CardAnimation {
    private Pane gamePane;
    private double deckX;
    private double deckY;
    private GUI gui;
    private StackPane deck;
    private int animationSpeed = 200; // default speed in milliseconds
    
    public CardAnimation(Pane gamePane, double deckX, double deckY, GUI gui) {
        this.gamePane = gamePane;
        this.deckX = deckX;
        this.deckY = deckY;
        this.gui = gui;
    }
    
    public StackPane createDeck() {
        deck = gui.createDeckCard();
        deck.setLayoutX(deckX);
        deck.setLayoutY(deckY);
        return deck;
    }
    
    public void setAnimationSpeed(int speedMilliseconds) {
        this.animationSpeed = speedMilliseconds;
    }
    
    // animates dealing the initial 4 cards
    public void dealInitialCards(String p1Rank, String p1Suit, String d1Rank, String d1Suit,
                                  String p2Rank, String p2Suit,
                                  double playerX, double playerY, double dealerX, double dealerY,
                                  Runnable onAllComplete) {
        
        // player card 1
        dealCard(p1Rank, p1Suit, playerX, playerY, () -> {
            pause(animationSpeed, () -> {
                // dealer card 1
                dealCard(d1Rank, d1Suit, dealerX, dealerY, () -> {
                    pause(animationSpeed, () -> {
                        // player card 2
                        dealCard(p2Rank, p2Suit, playerX + 117, playerY, () -> {
                            pause(animationSpeed, () -> {
                                // dealer hidden card
                                dealHiddenCard(dealerX + 117, dealerY, () -> {
                                    pause(animationSpeed, () -> {
                                        // clear animated cards
                                        gamePane.getChildren().removeIf(node ->
                                            node instanceof StackPane && node != deck
                                        );
                                        
                                        if (onAllComplete != null) {
                                            onAllComplete.run();
                                        }
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    }
    
    // animates a single card (used for hit/double)
    public void dealSingleCard(String rank, String suit, double targetX, double targetY, Runnable onComplete) {
        dealCard(rank, suit, targetX, targetY, () -> {
            pause(animationSpeed / 2, () -> {
                // clear animated card
                gamePane.getChildren().removeIf(node ->
                    node instanceof StackPane && node != deck
                );
                
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        });
    }
    
    // animates a single card moving from deck to target
    private void dealCard(String rank, String suit, double targetX, double targetY, Runnable onComplete) {
        StackPane card = gui.createCard(rank, suit);
        card.setLayoutX(deckX);
        card.setLayoutY(deckY);
        
        gamePane.getChildren().add(card);
        
        TranslateTransition move = new TranslateTransition(Duration.millis(animationSpeed * 2.5), card);
        move.setToX(targetX - deckX);
        move.setToY(targetY - deckY);
        
        if (onComplete != null) {
            move.setOnFinished(e -> onComplete.run());
        }
        
        move.play();
    }
    
    // animates dealing a hidden card
    private void dealHiddenCard(double targetX, double targetY, Runnable onComplete) {
        StackPane card = gui.createHiddenCard();
        card.setLayoutX(deckX);
        card.setLayoutY(deckY);
        
        gamePane.getChildren().add(card);
        
        TranslateTransition move = new TranslateTransition(Duration.millis(animationSpeed * 2.5), card);
        move.setToX(targetX - deckX);
        move.setToY(targetY - deckY);
        
        if (onComplete != null) {
            move.setOnFinished(e -> onComplete.run());
        }
        
        move.play();
    }
    
    // pauses between card deals
    public void pause(int millis, Runnable onComplete) {
        PauseTransition pause = new PauseTransition(Duration.millis(millis));
        pause.setOnFinished(e -> onComplete.run());
        pause.play();
    }
    
    public int getAnimationSpeed() {
        return animationSpeed;
    }
}