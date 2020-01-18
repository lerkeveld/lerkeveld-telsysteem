import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class Runner {
    private Label nameLabel;
    private VBox outerBox = new VBox();
    private Label extraNameLabel;
    private Label extraValueLabel;
    private Label lapCount;
    private TimerHandler timerHandler;
    private ArrayList<Label> labels = new ArrayList<>();
    private double scale = 1;

    public Runner(String name, int laps, String extraName, String extraValue, Label timer) {
        outerBox.setAlignment(Pos.TOP_CENTER);
        outerBox.setPadding(new Insets(0, 10, 0, 10));
        outerBox.setStyle("-fx-border-color: grey; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");

        nameLabel = new Label(name);
        outerBox.getChildren().add(nameLabel);
        nameLabel.setFont(new Font("System Bold", 48.0));
        labels.add(nameLabel);

        HBox info = new HBox();
        outerBox.getChildren().add(info);
        info.setAlignment(Pos.TOP_CENTER);
        info.setSpacing(10);

        Label lapsLabel = new Label("Aantal Rondjes:");
        info.getChildren().add(lapsLabel);
        lapsLabel.setFont(new Font("System Bold", 24.0));
        labels.add(lapsLabel);

        lapCount = new Label("" + laps);
        info.getChildren().add(lapCount);
        lapCount.setFont(new Font("System", 24.0));
        labels.add(lapCount);

        extraNameLabel = new Label(extraName);
        info.getChildren().add(extraNameLabel);
        extraNameLabel.setFont(new Font("System Bold", 24.0));
        labels.add(extraNameLabel);

        extraValueLabel = new Label(extraValue);
        info.getChildren().add(extraValueLabel);
        extraValueLabel.setFont(new Font("System", 24.0));
        labels.add(extraValueLabel);

        timerHandler = new TimerHandler(timer);
    }

    public void setParent(Pane parent) {
        scale(1 / scale);
        parent.getChildren().setAll(outerBox);
        outerBox.prefHeightProperty().bind(parent.heightProperty());
        outerBox.prefWidthProperty().bind(parent.widthProperty());
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return timerHandler;
    }

    public void incrementLapCount() {
        lapCount.setText("" + (Integer.parseInt(lapCount.getText()) + 1));
    }

    public String getRunTime() {
        return TimerHandler.toText(timerHandler.getTime());
    }

    public void setExtra(String name, String value) {
        extraNameLabel.setText(name);
        extraValueLabel.setText(value);
    }

    public void changeTextColor(Color color) {
        labels.forEach(label -> label.setTextFill(color));
    }

    public void scale(double i) {
        if (i > 0) {
            labels.forEach(label -> label.setFont(new Font(label.getFont().getName(), label.getFont().getSize() * i)));
            scale *= i;
        }
    }

    public int getTime() {
        return timerHandler.getTime();
    }

    public String getName() {
        return nameLabel.getText();
    }
}
