package lerkeveld.telsysteem.countSystem.mainElements;

import lerkeveld.telsysteem.countSystem.CountSystem;
import lerkeveld.telsysteem.countSystem.utilities.TimerHandler;
import lerkeveld.telsysteem.countSystem.supportElements.Runner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

// keeps track of the time in the current lap
// shows the runner that is currently running
public class Stopwatch extends VBox {

    // label that shows the duration of the current lap
    private Label timer = new Label();

    // The runner that is running the current lap
    // initialized as null, as there is no runner at the start
    private Runner runner;

    // the timeline for setting animations
    private Timeline timeline = new Timeline();

    // the button that starts a new lap
    private Button button = new Button("Start");

    // whether a lap is going or not
    private TimerHandler timerHandler = new TimerHandler(timer);
    private CountSystem countSystem;

    public Stopwatch(CountSystem countSystem) {
        super();
        this.countSystem = countSystem;

        // layout of the VBox
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");
        setSpacing(10);

        // get an empty runner template, can not use setRunner(), as it requires the VBox to be populated.
        runner = Runner.getEmptyRunner();
        runner.maxWidthProperty().bind(widthProperty().subtract(20));

        // populate the VBox
        getChildren().addAll(timer, runner, button);

        // button setting
        button.setOnAction(actionEvent -> countSystem.nextLap());
        button.maxWidthProperty().bind(widthProperty().subtract(20));

        // set font of time label
        timer.setFont(new Font("System Bold", 106));

        // set initial text of time label
        timer.setText("0:00.0");

        // timeline settings
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // start a new lap and stop the previous lap if it is not stopped
    public Runner startNewLap(Runner runner) {
        Runner oldRunner = stopCurrentLap();
        timerHandler = new TimerHandler(timer);
        timeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(0.1), timerHandler));
        setRunner(runner);
        timeline.play();
        return oldRunner;
    }

    // stop the current lap and get the time
    public Runner stopCurrentLap() {
        // reset time label
        int time = timerHandler.getTime();
        timeline.stop();
        timer.setText("0:00.0");
        Runner oldRunner = runner;
        setRunner(Runner.getEmptyRunner());
        oldRunner.setExtra("Lap tijd:", TimerHandler.toText(time));
        if (!oldRunner.isEmpty()) countSystem.registerLap(oldRunner.getName(), time);
        return oldRunner;
    }

    // set the next runner
    private void setRunner(Runner r) {
        runner = r;
        getChildren().set(1, r);
    }

    public Runner getRunner(){
        return runner;
    }

    // scale all elements of this stopwatch
    public void scale(double s) {
        // scale stopwatch label
        timer.setFont(new Font(timer.getFont().getName(), timer.getFont().getSize() * s));
        // scale runner
        runner.scale(s);
        // scale button
        button.setFont(new Font(button.getFont().getName(), button.getFont().getSize() * s));
    }
}