import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class SettingsDialog {
    
    public static class SettingsResult {
        public final int animationSpeed;
        public final boolean showRunningCount;
        public final boolean showTrueCount;
        public final boolean showCombinedCount;
        
        public SettingsResult(int animationSpeed, boolean showRunningCount, 
                            boolean showTrueCount, boolean showCombinedCount) {
            this.animationSpeed = animationSpeed;
            this.showRunningCount = showRunningCount;
            this.showTrueCount = showTrueCount;
            this.showCombinedCount = showCombinedCount;
        }
    }
    
    public static SettingsResult show(int currentSpeed, boolean currentRunningCount, 
                                     boolean currentTrueCount, boolean currentCombinedCount) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Game Settings");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Speed section - convert ms to x multiplier
        String speedText = getSpeedMultiplier(currentSpeed);
        Label speedLabel = new Label("Current Speed: " + speedText);
        speedLabel.setStyle("-fx-font-size: 14px;");
        
        Button changeSpeedButton = new Button("Change Speed");
        changeSpeedButton.setStyle("-fx-font-size: 12px;");
        
        final int[] selectedSpeed = {currentSpeed}; // Array to hold mutable value
        
        changeSpeedButton.setOnAction(e -> {
            Integer newSpeed = showSpeedDialog(selectedSpeed[0]);
            if (newSpeed != null) {
                selectedSpeed[0] = newSpeed;
                speedLabel.setText("Current Speed: " + getSpeedMultiplier(newSpeed));
            }
        });
        
        HBox speedBox = new HBox(15);
        speedBox.getChildren().addAll(speedLabel, changeSpeedButton);
        
        // Count display checkboxes
        CheckBox runningCountCheck = new CheckBox("Toggle Running Count");
        runningCountCheck.setSelected(currentRunningCount);
        runningCountCheck.setStyle("-fx-font-size: 13px;");
        
        CheckBox trueCountCheck = new CheckBox("Toggle True Count");
        trueCountCheck.setSelected(currentTrueCount);
        trueCountCheck.setStyle("-fx-font-size: 13px;");
        
        CheckBox combinedCountCheck = new CheckBox("Toggle Combined Count");
        combinedCountCheck.setSelected(currentCombinedCount);
        combinedCountCheck.setStyle("-fx-font-size: 13px;");
        
        Tooltip combinedTooltip = new Tooltip("Shows: Running Count (True Count)\nExample: Count: 12 (3.45)");
        combinedTooltip.setShowDelay(javafx.util.Duration.millis(100));
        combinedCountCheck.setTooltip(combinedTooltip);
        
        // Layout
        grid.add(speedBox, 0, 0, 2, 1);
        grid.add(new Separator(), 0, 1, 2, 1);
        grid.add(new Label("Count Display Options:"), 0, 2, 2, 1);
        grid.add(runningCountCheck, 0, 3, 2, 1);
        grid.add(trueCountCheck, 0, 4, 2, 1);
        grid.add(combinedCountCheck, 0, 5, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        return dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .map(response -> new SettingsResult(
                selectedSpeed[0],
                runningCountCheck.isSelected(),
                trueCountCheck.isSelected(),
                combinedCountCheck.isSelected()
            ))
            .orElse(null);
    }
    
    private static String getSpeedMultiplier(int speed) {
        switch (speed) {
            case 200: return "1x";
            case 100: return "2x";
            case 50: return "4x";
            case 25: return "8x";
            case 15: return "12x";
            default: return speed + "ms";
        }
    }
    
    private static Integer showSpeedDialog(int currentSpeed) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Animation Speed");
        dialog.setHeaderText("Select Animation Speed");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ToggleGroup speedGroup = new ToggleGroup();
        
        RadioButton defaultSpeed = new RadioButton("1x Speed (200ms)");
        RadioButton speed2x = new RadioButton("2x Speed (100ms)");
        RadioButton speed4x = new RadioButton("4x Speed (50ms)");
        RadioButton speed8x = new RadioButton("8x Speed (25ms)");
        RadioButton speed12x = new RadioButton("12x Speed (15ms)");
        
        defaultSpeed.setToggleGroup(speedGroup);
        speed2x.setToggleGroup(speedGroup);
        speed4x.setToggleGroup(speedGroup);
        speed8x.setToggleGroup(speedGroup);
        speed12x.setToggleGroup(speedGroup);
        
        // Select current speed
        if (currentSpeed == 100) speed2x.setSelected(true);
        else if (currentSpeed == 50) speed4x.setSelected(true);
        else if (currentSpeed == 25) speed8x.setSelected(true);
        else if (currentSpeed == 15) speed12x.setSelected(true);
        else defaultSpeed.setSelected(true);
        
        grid.add(defaultSpeed, 0, 0);
        grid.add(speed2x, 0, 1);
        grid.add(speed4x, 0, 2);
        grid.add(speed8x, 0, 3);
        grid.add(speed12x, 0, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        return dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .map(response -> {
                if (speed2x.isSelected()) return 100;
                if (speed4x.isSelected()) return 50;
                if (speed8x.isSelected()) return 25;
                if (speed12x.isSelected()) return 15;
                return 200;
            })
            .orElse(null);
    }
}