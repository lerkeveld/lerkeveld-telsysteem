import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PreviousLaps {

    private VBox laps = new VBox();

    public PreviousLaps(Pane parent) {
        laps.setAlignment(Pos.TOP_CENTER);
        laps.setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");
        laps.getChildren().setAll(new AnchorPane(), new AnchorPane(), new AnchorPane());

        parent.getChildren().setAll(laps);
        laps.prefHeightProperty().bind(parent.heightProperty());
        laps.prefWidthProperty().bind(parent.widthProperty());

        laps.getChildren().forEach(pane -> {
            ((Pane) pane).prefHeightProperty().bind(laps.heightProperty().divide(3));
            ((Pane) pane).prefWidthProperty().bind(laps.widthProperty());
        });
    }

    public void pushLap(Runner runner) {
        ((Pane) laps.getChildren().get(2)).getChildren().setAll(((Pane) laps.getChildren().get(1)).getChildren());
        ((Pane) laps.getChildren().get(1)).getChildren().setAll(((Pane) laps.getChildren().get(0)).getChildren());
        runner.setParent((Pane) laps.getChildren().get(0));
        runner.incrementLapCount();
        runner.setExtra("Ronde Tijd:", runner.getRunTime());
        runner.changeTextColor(Color.SLATEGREY);
    }

}
