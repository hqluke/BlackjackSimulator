import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: darkgreen;");

        // top text
        Label titleLabel = new Label("Blackjack Simulator");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(10));
        root.setTop(titleLabel);

        // card area
        VBox centerArea = new VBox(40);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setStyle("-fx-padding: 20px;");

        // dealer area
        VBox dealerSection = new VBox(10);
        dealerSection.setAlignment(Pos.CENTER);
        
        Label dealerLabel = new Label("Dealer");
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        HBox dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        //hard coded dealer cards
        dealerCards.getChildren().addAll(
            createCard("K", "♠", true),
            createHiddenCard()
        );
        
        dealerSection.getChildren().addAll(dealerLabel, dealerCards);

        //player area
        VBox playerSection = new VBox(10);
        playerSection.setAlignment(Pos.CENTER);
        
        Label playerLabel = new Label("Player");
        playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        HBox playerCards = new HBox(10);
        playerCards.setAlignment(Pos.CENTER);
        //hard coded player cards
        playerCards.getChildren().addAll(
            createCard("A", "♥", false),
            createCard("10", "♦", false)
        );
        
        Label playerValueLabel = new Label("Value: 21 (Blackjack!)");
        playerValueLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: yellow; -fx-font-weight: bold;");
        
        playerSection.getChildren().addAll(playerLabel, playerCards, playerValueLabel);

        centerArea.getChildren().addAll(dealerSection, playerSection);
        root.setCenter(centerArea);

        // button area
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setStyle("-fx-padding: 15px;");

        // actual buttons
        Button hitButton = new Button("Hit");
        Button standButton = new Button("Stand");
        Button doubleButton = new Button("Double");
        Button splitButton = new Button("Split");
        
        // button styles
        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 10px 20px;";
        String transparentButtonStyle = buttonStyle + "-fx-opacity: 0.5;";
        
        hitButton.setStyle(buttonStyle);
        standButton.setStyle(buttonStyle);
        doubleButton.setStyle(transparentButtonStyle);
        splitButton.setStyle(transparentButtonStyle);

        buttonBar.getChildren().addAll(hitButton, standButton, doubleButton, splitButton);
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 800, 600); // remove hard coded sizing later?
        primaryStage.setTitle("Blackjack Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //card creator
    private StackPane createCard(String rank, String suit, boolean isBlack) {
        StackPane card = new StackPane();
        
        // card background
        Rectangle cardBg = new Rectangle(70, 100);
        cardBg.setFill(Color.WHITE);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);
        cardBg.setStroke(Color.BLACK);
        cardBg.setStrokeWidth(2);
        
        // card content
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.CENTER);
        
        Label rankLabel = new Label(rank);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label suitLabel = new Label(suit);
        suitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        // card color
        Color textColor = (suit.equals("♥") || suit.equals("♦")) ? Color.RED : Color.BLACK;
        rankLabel.setTextFill(textColor);
        suitLabel.setTextFill(textColor);
        
        cardContent.getChildren().addAll(rankLabel, suitLabel);
        
        card.getChildren().addAll(cardBg, cardContent);
        return card;
    }
    
    // dealers hidden card
    private StackPane createHiddenCard() {
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
}

