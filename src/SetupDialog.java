import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SetupDialog {
    
    public static class GameConfig {
        public final int numDecks;
        public final double startingMoney;
        public final double minimumBet;
        public final boolean isFullscreen;
        public final int animationSpeed;
        
        public GameConfig(int numDecks, double startingMoney, double minimumBet, int animationSpeed) {
            this.numDecks = numDecks;
            this.startingMoney = startingMoney;
            this.minimumBet = minimumBet;
            this.isFullscreen = true; // Always fullscreen
            this.animationSpeed = animationSpeed;
        }
    }
    
    public static GameConfig show() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Blackjack Setup");
        dialog.setHeaderText("Configure your game");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField decksField = new TextField("6");
        TextField moneyField = new TextField("1000");
        TextField minBetField = new TextField("10");
        
        // Animation speed options
        ToggleGroup speedGroup = new ToggleGroup();
        RadioButton defaultSpeed = new RadioButton("Default (200ms)");
        RadioButton speed2x = new RadioButton("2x Speed (100ms)");
        RadioButton speed4x = new RadioButton("4x Speed (50ms)");
        RadioButton speed8x = new RadioButton("8x Speed (25ms)");
        RadioButton speed12x = new RadioButton("12x Speed (15ms)");
        
        defaultSpeed.setToggleGroup(speedGroup);
        speed2x.setToggleGroup(speedGroup);
        speed4x.setToggleGroup(speedGroup);
        speed8x.setToggleGroup(speedGroup);
        speed12x.setToggleGroup(speedGroup);
        
        defaultSpeed.setSelected(true);
        
        VBox speedOptions = new VBox(5);
        speedOptions.getChildren().addAll(defaultSpeed, speed2x, speed4x, speed8x, speed12x);
        
        grid.add(new Label("Number of Decks (min 1):"), 0, 0);
        grid.add(decksField, 1, 0);
        grid.add(new Label("Starting Money:"), 0, 1);
        grid.add(moneyField, 1, 1);
        grid.add(new Label("Minimum Bet:"), 0, 2);
        grid.add(minBetField, 1, 2);
        grid.add(new Label("Animation Speed:"), 0, 3);
        grid.add(speedOptions, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        
        dialog.showAndWait();
        
        try {
            int numDecks = Integer.parseInt(decksField.getText());
            double startingMoney = Double.parseDouble(moneyField.getText());
            double minBet = Double.parseDouble(minBetField.getText());
            
            // Validate minimum 1 deck
            if (numDecks < 1) {
                numDecks = 1;
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Deck Count");
                alert.setHeaderText(null);
                alert.setContentText("Minimum 1 deck required. Setting to 1 deck.");
                alert.showAndWait();
            }
            
            // Get selected animation speed
            int animationSpeed = 200; // default
            if (speed2x.isSelected()) animationSpeed = 100;
            else if (speed4x.isSelected()) animationSpeed = 50;
            else if (speed8x.isSelected()) animationSpeed = 25;
            else if (speed12x.isSelected()) animationSpeed = 15;
            
            return new GameConfig(numDecks, startingMoney, minBet, animationSpeed);
            
        } catch (NumberFormatException ex) {
            System.out.println("Invalid setup values, using defaults");
            return new GameConfig(6, 1000, 10, 200);
        }
    }
}