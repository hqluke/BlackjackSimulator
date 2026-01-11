import javafx.application.Platform;
import javafx.scene.control.*;

public class InsuranceDialog {
    
    public static void show(Runnable onAccept, Runnable onDecline) {
        // Defer the dialog to the next frame after animations complete
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Insurance Offer");
            dialog.setHeaderText("Dealer is showing an Ace");
            dialog.setContentText("Would you like to take insurance?");
            
            dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Accept", ButtonBar.ButtonData.YES),
                new ButtonType("Decline", ButtonBar.ButtonData.NO)
            );
            
            dialog.showAndWait().ifPresent(response -> {
                if (response.getText().equals("Accept")) {
                    onAccept.run();
                } else {
                    onDecline.run();
                }
            });
        });
    }
}