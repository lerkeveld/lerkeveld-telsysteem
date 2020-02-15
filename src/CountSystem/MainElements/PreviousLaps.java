package CountSystem.MainElements;

import CountSystem.supportElements.Runner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// todo auto scaling of elements
public class PreviousLaps extends VBox {

    private double verticalPadding = 0;
    private double horizontalPadding = 0;
    private double verticalSpacing = 0;

    public PreviousLaps() {
        super();

        // VBox layout
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");
        setPadding(new Insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
        setSpacing(verticalSpacing);

        // populate VBox
        getChildren().setAll(Runner.getEmptyRunner(), Runner.getEmptyRunner(), Runner.getEmptyRunner());

        // set runner layout
        getChildren().forEach(node -> {
            ((Runner) node).prefWidthProperty().bind(widthProperty().subtract(2 * horizontalPadding));
            ((Runner) node).prefHeightProperty().bind(heightProperty().subtract(2 * verticalPadding + 2 * verticalSpacing).divide(3));
        });
    }

    public void pushLap(Runner runner) {
        // doew not accept emtpy runners
        if (runner.isEmpty()) return;
        getChildren().add(0, runner);
        getChildren().remove(3);

        //set runner width and height
        runner.maxWidthProperty().bind(widthProperty().subtract(2 * horizontalPadding));
        runner.maxHeightProperty().bind(heightProperty().subtract(2 * verticalPadding + 2 * verticalSpacing).divide(3));


        runner.incrementLapCount();
        runner.changeTextColor(Color.SLATEGREY);
    }

}
