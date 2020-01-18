import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class QueuedRunner {
    private int position;
    private Runner runner;
    private HBox frame = new HBox();
    private Label label = new Label("0");

    public QueuedRunner(Runner runner, RunnerQueue queue) {
        frame.setAlignment(Pos.CENTER_LEFT);

        this.position = queue.size() + 1;
        frame.getChildren().add(label);
        label.setText(Integer.toString(position));
        label.setFont(new Font("System Bold", 48.0));
        label.setAlignment(Pos.CENTER);
        label.prefHeightProperty().bind(frame.heightProperty());
        label.prefWidthProperty().bind(frame.heightProperty());
        label.setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        AnchorPane runnerPane = new AnchorPane();
        frame.getChildren().add(runnerPane);
        this.runner = runner;
        runner.setParent(runnerPane);

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
            ((Button) button).prefHeightProperty().bind(frame.heightProperty().divide(3));
            ((Button) button).prefWidthProperty().bind(frame.heightProperty().divide(3));
            ((Button) button).setFont(new Font("Font Awesome 5 Free Solid", 12));
        });
        frame.getChildren().add(buttons);
        runnerPane.prefWidthProperty().bind(frame.widthProperty().subtract(frame.heightProperty().multiply(4.0 / 3.0)));
    }

    public void addToParent(Pane queue) {
        queue.getChildren().add(frame);
        frame.prefWidthProperty().bind(queue.widthProperty());
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
