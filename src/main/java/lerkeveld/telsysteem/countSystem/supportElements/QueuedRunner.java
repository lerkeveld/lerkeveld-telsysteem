package lerkeveld.telsysteem.countSystem.supportElements;

import lerkeveld.telsysteem.countSystem.mainElements.RunnerQueue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

// wrapper around a runner to indicate its position in the queue
// and allow it to move up/down in the queue or get removed from it
public class QueuedRunner extends HBox {
    private VBox buttons;
    private int position;
    private Runner runner;
    private Label label = new Label("0");
    private static double defaultTextSize = 12;

    public QueuedRunner(Runner runner, RunnerQueue queue) {
        // set layout
        setAlignment(Pos.CENTER_LEFT);
        runner.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty().multiply(4).divide(3)));
        runner.scale(0.8);

        // add the queue position indicator
        this.position = queue.size() + 1;
        getChildren().add(label);
        label.setText(Integer.toString(position));
        label.setFont(new Font("System Bold", defaultTextSize*4));
        label.setAlignment(Pos.CENTER);
        label.minHeightProperty().bind(heightProperty());
        label.minWidthProperty().bind(heightProperty());
        label.setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        // add the runner
        getChildren().add(runner);
        this.runner = runner;

        // laqd the font used for the arrows and delete character
        Font.loadFont(this.getClass().getClassLoader().getResource("fa-solid-900.ttf").toExternalForm(), defaultTextSize);

        // setup the arrows and delete button
        buttons = new VBox();
        Button up = new Button("\uf077");
        up.setOnAction(event -> queue.moveUp(this));
        Button cancel = new Button("\uf05e");
        cancel.setOnAction(event -> queue.remove(this));
        Button down = new Button("\uf078");
        down.setOnAction(event -> queue.moveDown(this));
        buttons.getChildren().addAll(up, cancel, down);
        buttons.getChildren().forEach(button -> {
            ((Button) button).minHeightProperty().bind(heightProperty().divide(3));
            ((Button) button).minWidthProperty().bind(heightProperty().divide(3));
            ((Button) button).setFont(new Font("Font Awesome 5 Free Solid", 12));
        });
        getChildren().add(buttons);
    }

    // decrease the queue position indicator
    public void advance() {
        label.setText(Integer.toString(--position));
    }

    // increase the queue position indicator
    public void decline() {
        label.setText(Integer.toString(++position));
    }

    // get the runner wrapped in this QueuedRunner
    public Runner getRunner() {
        return runner;
    }

    // get the position of this Queued runner in its queue
    public int getPosition() {
        return position;
    }

    // scale all elements of this queuedRunner with the given scale
    public void scale(double s) {
        // scale position label
        label.setFont(new Font(label.getFont().getName(), label.getFont().getSize() * s));
        // scale runner
        runner.scale(s);
        // scale button text
        buttons.getChildren().forEach(node -> ((Button) node).setFont(new Font(((Button) node).getFont().getName(), ((Button) node).getFont().getSize() * s)));
    }

    public String getName(){
        return runner.getName();
    }

    public static void scaleNewQueuedRunners(double s) {
        defaultTextSize *= s;
    }

    public void updateLapCount() {
        runner.updateLapCount();
    }
}
