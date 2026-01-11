
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SideBetDialog {
    
    public static class SideBetResult {
        public final double perfectPairAmount;
        public final double twentyOnePlusThreeAmount;
        public final boolean saveForNextRounds;
        
        public SideBetResult(double perfectPairAmount, double twentyOnePlusThreeAmount, boolean saveForNextRounds) {
            this.perfectPairAmount = perfectPairAmount;
            this.twentyOnePlusThreeAmount = twentyOnePlusThreeAmount;
            this.saveForNextRounds = saveForNextRounds;
        }
    }
    
    // Static variables to remember last bet amounts if "save" was checked
    private static double savedPerfectPairAmount = 0;
    private static double saved21Plus3Amount = 0;
    private static boolean hasSavedBets = false;

    public static void show(double playerMoney, double minimumBet, java.util.function.Consumer<SideBetResult> onComplete) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Side Bet Options");
        dialog.setHeaderText("Place Optional Side Bets");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Perfect Pair section
        Label perfectPairLabel = new Label("Perfect Pair:");
        perfectPairLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Tooltip perfectPairTooltip = new Tooltip(
            "Perfect Pair Side Bet:\n\n" +
            "Pays if your first two cards are a pair.\n\n" +
            "Payouts:\n" +
            "• Mixed Pair (different color): 5:1\n" +
            "• Same Color Pair: 10:1\n" +
            "• Perfect Pair (same suit): 30:1"
        );
        perfectPairTooltip.setShowDelay(javafx.util.Duration.millis(100));
        Tooltip.install(perfectPairLabel, perfectPairTooltip);
        
        TextField perfectPairField = new TextField(hasSavedBets ? String.valueOf((int)savedPerfectPairAmount) : "0");
        perfectPairField.setPrefWidth(100);
        perfectPairField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                perfectPairField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // 21+3 section
        Label twentyOnePlusThreeLabel = new Label("21+3:");
        twentyOnePlusThreeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Tooltip twentyOnePlusThreeTooltip = new Tooltip(
            "21+3 Side Bet:\n\n" +
            "Pays if your first two cards plus the dealer's up card\n" +
            "make a poker hand.\n\n" +
            "Payouts:\n" +
            "• Flush (same suit): 5:1\n" +
            "• Straight (consecutive ranks): 10:1\n" +
            "• Three of a Kind: 30:1\n" +
            "• Straight Flush: 40:1\n" +
            "• Suited Three of a Kind: 100:1"
        );
        twentyOnePlusThreeTooltip.setShowDelay(javafx.util.Duration.millis(100));
        Tooltip.install(twentyOnePlusThreeLabel, twentyOnePlusThreeTooltip);
        
        TextField twentyOnePlusThreeField = new TextField(hasSavedBets ? String.valueOf((int)saved21Plus3Amount) : "0");
        twentyOnePlusThreeField.setPrefWidth(100);
        twentyOnePlusThreeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                twentyOnePlusThreeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Money display
        Label moneyLabel = new Label(String.format("Available Money: $%.2f", playerMoney));
        moneyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
        
        // Save preference checkbox
        CheckBox saveCheckBox = new CheckBox("Remember these bets for next rounds");
        saveCheckBox.setSelected(hasSavedBets);
        
        // Layout
        grid.add(perfectPairLabel, 0, 0);
        grid.add(perfectPairField, 1, 0);
        grid.add(new Label("$"), 2, 0);
        
        grid.add(twentyOnePlusThreeLabel, 0, 1);
        grid.add(twentyOnePlusThreeField, 1, 1);
        grid.add(new Label("$"), 2, 1);
        
        VBox mainBox = new VBox(15);
        mainBox.getChildren().addAll(moneyLabel, grid, saveCheckBox);
        mainBox.setAlignment(Pos.CENTER);
        
        dialog.getDialogPane().setContent(mainBox);
        
        ButtonType placeButton = new ButtonType("Place Bets", ButtonBar.ButtonData.OK_DONE);
        ButtonType skipButton = new ButtonType("Skip", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(placeButton, skipButton);
        
        // Validate inputs before closing
        Button placeBetsButton = (Button) dialog.getDialogPane().lookupButton(placeButton);
        placeBetsButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                String ppText = perfectPairField.getText().trim();
                String ttText = twentyOnePlusThreeField.getText().trim();
                
                double ppAmount = ppText.isEmpty() ? 0 : Double.parseDouble(ppText);
                double ttAmount = ttText.isEmpty() ? 0 : Double.parseDouble(ttText);
                
                // Validate positive amounts
                if (ppAmount < 0 || ttAmount < 0) {
                    showError("Bet amounts must be 0 or positive numbers.");
                    event.consume();
                    return;
                }
                
                double totalBet = ppAmount + ttAmount;
                
                // Check if player has enough money
                if (totalBet > playerMoney) {
                    showError(String.format("Insufficient funds! You only have $%.2f available.", playerMoney));
                    event.consume();
                    return;
                }

                // Check if player has enough money left for a minimum bet after side bets
                if (totalBet > 0 && (playerMoney - totalBet) < minimumBet) {
                    showError(String.format("You must have at least $%.0f remaining after side bets to place a main bet.", minimumBet));
                    event.consume();
                    return;
                }
                
            } catch (NumberFormatException ex) {
                showError("Please enter valid whole numbers.");
                event.consume();
            }
        });
        
        dialog.showAndWait().ifPresent(response -> {
            SideBetResult result;
            if (response == skipButton) {
                result = new SideBetResult(0, 0, false);
            } else {
                // Parse the amounts
                String ppText = perfectPairField.getText().trim();
                String ttText = twentyOnePlusThreeField.getText().trim();
                
                double ppAmount = ppText.isEmpty() ? 0 : Double.parseDouble(ppText);
                double ttAmount = ttText.isEmpty() ? 0 : Double.parseDouble(ttText);
                boolean shouldSave = saveCheckBox.isSelected();
                
                result = new SideBetResult(ppAmount, ttAmount, shouldSave);
                
                // Update saved values if checkbox is selected
                if (shouldSave) {
                    savedPerfectPairAmount = ppAmount;
                    saved21Plus3Amount = ttAmount;
                    hasSavedBets = true;
                } else {
                    hasSavedBets = false;
                }
            }
            
            if (onComplete != null) {
                onComplete.accept(result);
            }
        });
    }
    
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Bet");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Method to clear saved bets (call when player wants to reset)
    public static void clearSavedBets() {
        savedPerfectPairAmount = 0;
        saved21Plus3Amount = 0;
        hasSavedBets = false;
    }
}