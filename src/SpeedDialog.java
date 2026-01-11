import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SpeedDialog {
    
    public static Integer show() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Animation Speed");
        dialog.setHeaderText("Select Animation Speed");
        
        VBox vbox = new VBox(10);
        
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
        
        vbox.getChildren().addAll(defaultSpeed, speed2x, speed4x, speed8x, speed12x);
        
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        return dialog.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .map(response -> {
                if (speed2x.isSelected()) return 100;
                if (speed4x.isSelected()) return 50;
                if (speed8x.isSelected()) return 25;
                if (speed12x.isSelected()) return 15;
                return 200; // default
            })
            .orElse(null);
    }
}