package CountSystem.supportElements;

import CountSystem.MainElements.RunnerQueue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class QueuedRunner extends HBox {
    private int position;
    private Runner runner;
    private Label label = new Label("0");

    public QueuedRunner(Runner runner, RunnerQueue queue) {
        setAlignment(Pos.CENTER_LEFT);
        runner.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty().multiply(4).divide(3)));
        runner.scale(0.8);

        this.position = queue.size() + 1;
        getChildren().add(label);
        label.setText(Integer.toString(position));
        label.setFont(new Font("System Bold", 48.0));
        label.setAlignment(Pos.CENTER);
        label.minHeightProperty().bind(heightProperty());
        label.minWidthProperty().bind(heightProperty());
        label.setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        getChildren().add(runner);
        this.runner = runner;

        Font.loadFont(this.getClass().getClassLoader().getResource("fa-solid-900.ttf").toExternalForm(), 12);

        VBox buttons = new VBox();
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

    public void advance() {
        label.setText(Integer.toString(--position));
    }

    public void decline() {
        label.setText(Integer.toString(++position));
    }

    public Runner getRunner() {
        return runner;
    }

    public int getPosition() {
        return position;
    }
}
