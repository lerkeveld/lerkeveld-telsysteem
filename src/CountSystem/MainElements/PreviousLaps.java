package CountSystem.MainElements;

import CountSystem.supportElements.Runner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// keeps track of the last N laps
public class PreviousLaps extends VBox {

    public static final int N = 3;
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
            ((Runner) node).prefHeightProperty().bind(heightProperty().subtract(2 * verticalPadding + (N - 1) * verticalSpacing).divide(N));
        });
    }

    // add a new lap to the preview, remove the last one
    public void pushLap(Runner runner) {
        // does not accept empty runners
        if (runner.isEmpty()) return;
        getChildren().add(0, runner);
        getChildren().remove(N);

        //set runner width and height
        runner.maxWidthProperty().bind(widthProperty().subtract(2 * horizontalPadding));
        runner.maxHeightProperty().bind(heightProperty().subtract(2 * verticalPadding + 2 * verticalSpacing).divide(3));

        // update runner information and style
        runner.incrementLapCount();
        runner.changeTextColor(Color.SLATEGREY);
    }

    // scale all runners in this queue
    public void scale(double s) {
        getChildren().forEach(node -> ((Runner) node).scale(s));
    }
}
