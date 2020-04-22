package lerkeveld.telsysteem.countSystem.supportElements;

import lerkeveld.telsysteem.countSystem.utilities.RunDatabase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

// small container with the name of the runner and some extra information
public class Runner extends VBox {

    private static double defaultTextSize = 23;
    private Label nameLabel;
    private Label extraNameLabel;
    private Label extraValueLabel;
    private Label lapCount;
    private ArrayList<Label> labels = new ArrayList<>(); // list of all labels for easy scaling
    private boolean empty = false;
    private RunDatabase database;

    public Runner(String name, int laps, String extraName, String extraValue, RunDatabase database) {
        super();
        this.database = database;
        // set layout of the VBox
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(0, 10, 0, 10));
        setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        // create name label
        nameLabel = new Label(name);
        getChildren().add(nameLabel);
        nameLabel.setFont(new Font("System Bold", 2 * defaultTextSize));
        labels.add(nameLabel);

        // create HBox containing extra information
        HBox info = new HBox();
        getChildren().add(info);
        info.setAlignment(Pos.TOP_CENTER);
        info.setSpacing(10);

        // create label for laps and lapcount
        Label lapsLabel = new Label("Aantal Rondjes:");
        info.getChildren().add(lapsLabel);
        lapsLabel.setFont(new Font("System Bold", defaultTextSize));
        labels.add(lapsLabel);

        lapCount = new Label("" + laps);
        info.getChildren().add(lapCount);
        lapCount.setFont(new Font("System", defaultTextSize));
        labels.add(lapCount);

        // create label for extra info and extra value
        extraNameLabel = new Label(extraName);
        info.getChildren().add(extraNameLabel);
        extraNameLabel.setFont(new Font("System Bold", defaultTextSize));
        labels.add(extraNameLabel);

        extraValueLabel = new Label(extraValue);
        info.getChildren().add(extraValueLabel);
        extraValueLabel.setFont(new Font("System", defaultTextSize));
        labels.add(extraValueLabel);
    }

    // returns an empty runner template
    public static Runner getEmptyRunner() {
        // new runner with space as all it's attributes.
        Runner runner = new Runner(" ", 0, " ", " ", null);
        ((HBox) runner.getChildren().get(1)).getChildren().forEach(child -> ((Label) child).setText(" "));
        // delete the given style
        runner.setStyle("");
        runner.setEmpty();
        return runner;
    }

    public static void scaleNewRunners(double s) {
        defaultTextSize *= s;
    }

    //  increment the lap count of this runner
    public void updateLapCount() {
        // does not work on empty runner
        if (!isEmpty()) lapCount.setText("" + database.getLapCount(nameLabel.getText()));
    }

    // set the extra information of this runner with the given name and value
    public void setExtra(String name, String value) {
        // does not work on empty runner
        if (isEmpty()) return;
        extraNameLabel.setText(name);
        extraValueLabel.setText(value);
    }

    // change the text color of all labels
    public void changeTextColor(Color color) {
        labels.forEach(label -> label.setTextFill(color));
    }

    // scale all labels by the given scale factor
    public void scale(double s) {
        labels.forEach(label -> label.setFont(new Font(label.getFont().getName(), label.getFont().getSize() * s)));
    }

    // set the indicator for whether this is an dummy runner
    private void setEmpty() {
        empty = true;
    }

    // check whether this is a dummy runner
    public boolean isEmpty() {
        return empty;
    }

    // get the name of this runner
    public String getName() {
        return nameLabel.getText();
    }
}
