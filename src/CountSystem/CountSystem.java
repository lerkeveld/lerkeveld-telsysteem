package CountSystem;

import CountSystem.MainElements.PreviousLaps;
import CountSystem.MainElements.Registrator;
import CountSystem.MainElements.RunnerQueue;
import CountSystem.MainElements.StopWatch;
import CountSystem.Utilities.RunDatabase;
import CountSystem.supportElements.Runner;
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

    private StopWatch stopWatch;
    private Registrator registrator;
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
        stopWatch = new StopWatch(this);
        registrator = new Registrator(this, database);
        previousLaps = new PreviousLaps();
        runnerQueue = new RunnerQueue();
        vBox1.getChildren().setAll(stopWatch, previousLaps);
        vBox2.getChildren().setAll(registrator, runnerQueue);

        // set layout of main elements
        stopWatch.prefHeightProperty().bind(vBox1.heightProperty().subtract(verticalSpacing).divide(2));
        stopWatch.prefWidthProperty().bind(vBox1.widthProperty());
        previousLaps.prefHeightProperty().bind(vBox1.heightProperty().subtract(verticalSpacing).divide(2));
        previousLaps.prefWidthProperty().bind(vBox1.widthProperty());
        runnerQueue.prefHeightProperty().bind(vBox2.heightProperty().subtract(verticalSpacing).divide(2));
        runnerQueue.prefWidthProperty().bind(vBox2.widthProperty());
        registrator.prefHeightProperty().bind(vBox2.heightProperty().subtract(verticalSpacing).divide(2));
        registrator.prefWidthProperty().bind(vBox2.widthProperty());
    }

    // interface between stopwatch, runnerQueue and previousLaps
    public void nextLap() {
        Runner nextRunner = runnerQueue.pop();
        if (Objects.isNull(nextRunner)) {
            previousLaps.pushLap(stopWatch.stopCurrentLap());
        } else previousLaps.pushLap(stopWatch.startNewLap(nextRunner));
    }

    // interface between runnerQueue and database
    public void registerLap(String name, int time) {
        database.addLap(name, time);
    }

    // interface between database and runnerRegistration
    public void changeToRunnerRegistration() {
        registrator.changeToRunnerRegistration();
    }

    // interface between runnerRegistration and runnerQueue
    public void addRunnerToQueue(Runner runner) {
        runnerQueue.add(runner);
    }
}
