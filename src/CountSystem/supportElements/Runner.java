package CountSystem.supportElements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class Runner extends VBox {

    private static final double defaultTextSize = 23;
    private Label nameLabel;
    private Label extraNameLabel;
    private Label extraValueLabel;
    private Label lapCount;
    private ArrayList<Label> labels = new ArrayList<>();
    private boolean empty = false;

    public Runner(String name, int laps, String extraName, String extraValue) {
        super();
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(0, 10, 0, 10));
        setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        nameLabel = new Label(name);
        getChildren().add(nameLabel);
        nameLabel.setFont(new Font("System Bold", 2 * defaultTextSize));
        labels.add(nameLabel);

        HBox info = new HBox();
        getChildren().add(info);
        info.setAlignment(Pos.TOP_CENTER);
        info.setSpacing(10);

        Label lapsLabel = new Label("Aantal Rondjes:");
        info.getChildren().add(lapsLabel);
        lapsLabel.setFont(new Font("System Bold", defaultTextSize));
        labels.add(lapsLabel);

        lapCount = new Label("" + laps);
        info.getChildren().add(lapCount);
        lapCount.setFont(new Font("System", defaultTextSize));
        labels.add(lapCount);

        extraNameLabel = new Label(extraName);
        info.getChildren().add(extraNameLabel);
        extraNameLabel.setFont(new Font("System Bold", defaultTextSize));
        labels.add(extraNameLabel);

        extraValueLabel = new Label(extraValue);
        info.getChildren().add(extraValueLabel);
        extraValueLabel.setFont(new Font("System", defaultTextSize));
        labels.add(extraValueLabel);

        // set autoScale feature todo make better
//        heightProperty().addListener((observableValue, old, n) -> scale(old.doubleValue() > 10 ? n.doubleValue()/old.doubleValue() : 1));
//        widthProperty().addListener((observableValue, old, n) -> scale(old.doubleValue() > 100 ? n.doubleValue()/old.doubleValue() : 1));
    }

    // returns an empty runner template
    public static Runner getEmptyRunner() {
        // new runner with space as all it's attributes.
        Runner runner = new Runner(" ", 0, " ", " ");
        ((HBox) runner.getChildren().get(1)).getChildren().forEach(child -> ((Label) child).setText(" "));
        // delete the given style
        runner.setStyle("");
        runner.setEmpty();
        return runner;
    }

    public void incrementLapCount() {
        // does not work on empty runner
        if (isEmpty()) return;
        lapCount.setText("" + (Integer.parseInt(lapCount.getText()) + 1));
    }

    public void setExtra(String name, String value) {
        // does not work on empty runner
        if (isEmpty()) return;
        extraNameLabel.setText(name);
        extraValueLabel.setText(value);
    }

    public void changeTextColor(Color color) {
        labels.forEach(label -> label.setTextFill(color));
    }

    public void scale(double i) {
        if (i > 0) {
            labels.forEach(label -> label.setFont(new Font(label.getFont().getName(), label.getFont().getSize() * i)));
        }
    }

    private void setEmpty() {
        empty = true;
    }

    public boolean isEmpty() {
        return empty;
    }

    public String getName() {
        return nameLabel.getText();
    }
}
