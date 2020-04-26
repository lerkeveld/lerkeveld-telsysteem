package lerkeveld.telsysteem.countSystem;

import lerkeveld.telsysteem.countSystem.mainElements.PreviousLaps;
import lerkeveld.telsysteem.countSystem.mainElements.Registration;
import lerkeveld.telsysteem.countSystem.mainElements.RunnerQueue;
import lerkeveld.telsysteem.countSystem.mainElements.Stopwatch;
import lerkeveld.telsysteem.countSystem.utilities.RunDatabase;
import lerkeveld.telsysteem.countSystem.supportElements.QueuedRunner;
import lerkeveld.telsysteem.countSystem.supportElements.Runner;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

// the app consists of four main elements, displayed in a grid pattern
public class CountSystem extends Scene {

    private double verticalPadding = 10;
    private double horizontalPadding = 10;
    private double horizontalSpacing = 10;
    private double verticalSpacing = 10;

    private Stopwatch stopWatch;
    private Registration registration;
    private PreviousLaps previousLaps;
    private RunnerQueue runnerQueue;
    private RunDatabase database;

    public CountSystem() {
        super(new HBox());

        HBox hBox = (HBox) getRoot();
        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();

        // set layout of hBox
        hBox.setPadding(new Insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
        hBox.setSpacing(horizontalSpacing);

        // populate hBox
        hBox.getChildren().setAll(vBox1, vBox2);

        // set layout of vBoxes
        vBox1.prefHeightProperty().bind(hBox.heightProperty().subtract(2 * verticalPadding));
        vBox1.prefWidthProperty().bind(hBox.widthProperty().subtract(2 * horizontalPadding + horizontalSpacing).divide(2));
        vBox2.prefHeightProperty().bind(hBox.heightProperty().subtract(2 * verticalPadding));
        vBox2.prefWidthProperty().bind(hBox.widthProperty().subtract(2 * horizontalPadding + horizontalSpacing).divide(2));
        vBox1.setSpacing(verticalSpacing);
        vBox2.setSpacing(verticalSpacing);

        // initialise database, not yet useful
        database = new RunDatabase(this);

        // populate vBoxes
        stopWatch = new Stopwatch(this);
        registration = new Registration(this, database);
        previousLaps = new PreviousLaps();
        runnerQueue = new RunnerQueue();
        vBox1.getChildren().setAll(stopWatch, previousLaps);
        vBox2.getChildren().setAll(registration, runnerQueue);

        // set layout of main elements
        stopWatch.prefHeightProperty().bind(vBox1.heightProperty().subtract(verticalSpacing).divide(2));
        stopWatch.prefWidthProperty().bind(vBox1.widthProperty());
        previousLaps.prefHeightProperty().bind(vBox1.heightProperty().subtract(verticalSpacing).divide(2));
        previousLaps.prefWidthProperty().bind(vBox1.widthProperty());
        runnerQueue.prefHeightProperty().bind(vBox2.heightProperty().subtract(verticalSpacing).divide(2));
        runnerQueue.prefWidthProperty().bind(vBox2.widthProperty());
        registration.prefHeightProperty().bind(vBox2.heightProperty().subtract(verticalSpacing).divide(2));
        registration.prefWidthProperty().bind(vBox2.widthProperty());

        // set listener that change the scale of all elements based on the height of the frame
        this.heightProperty().addListener((observableValue, s, t1) -> scale(t1.doubleValue() / s.doubleValue()));
    }

    // scale all elements of this count system with the given scale
    private void scale(double s) {
        if (0.1 < s && s < 10) {
            stopWatch.scale(s);
            previousLaps.scale(s);
            runnerQueue.scale(s);
            registration.scale(s);
            Runner.scaleNewRunners(s);
            QueuedRunner.scaleNewQueuedRunners(s);
        }
    }

    // interface between stopwatch, runnerQueue and previousLaps
    public void nextLap() {
        Runner nextRunner = runnerQueue.pop();
        Runner prevRunner = Objects.isNull(nextRunner) ? stopWatch.stopCurrentLap() : stopWatch.startNewLap(nextRunner);
        previousLaps.pushLap(prevRunner);
        // update the lap count of runners that just ran and are in the queue
        runnerQueue.updateRunner(prevRunner.getName());
        // update the lap count of the new runner
        if (!Objects.isNull(nextRunner)) nextRunner.updateLapCount();
    }

    // interface between runnerQueue and database
    public void registerLap(String name, int time) {
        database.addLap(name, time);
    }

    // interface between database and runnerRegistration
    public void changeToRunnerRegistration() {
        registration.changeToRunnerRegistration();
    }

    // interface between runnerRegistration and runnerQueue
    public void addRunnerToQueue(Runner runner) {
        runnerQueue.add(runner);
    }
}
