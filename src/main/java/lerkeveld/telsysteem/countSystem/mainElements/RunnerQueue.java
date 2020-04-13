package lerkeveld.telsysteem.countSystem.mainElements;

import lerkeveld.telsysteem.countSystem.supportElements.QueuedRunner;
import lerkeveld.telsysteem.countSystem.supportElements.Runner;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.LinkedList;

// a queue that keeps track of all registered runners
public class RunnerQueue extends ScrollPane {
    private VBox queue = new VBox();
    private LinkedList<QueuedRunner> runners = new LinkedList<>();

    public RunnerQueue() {
        super();

        // ScrollPane layout
        setFitToWidth(true);
        setContent(queue);

        // VBox layout
        queue.setAlignment(Pos.TOP_CENTER);
    }

    // add a runner to the end of the queue
    public void add(Runner runner) {
        QueuedRunner queuedRunner = new QueuedRunner(runner, this);
        runners.add(queuedRunner);
        queue.getChildren().add(queuedRunner);
    }

    // get the length of the queue
    public int size() {
        return runners.size();
    }

    // get the first runner in the queue and remove it from the queue
    public Runner pop() {
        // returns null if no runner is present
        if (size() == 0) return null;
        queue.getChildren().remove(0);
        runners.forEach(QueuedRunner::advance); // move all other runners one up in the queue
        return runners.pop().getRunner();
    }

    // move the given QueuedRunner one up in the queue
    // the logical boundary conditions apply
    public void moveUp(QueuedRunner queuedRunner) {
        int pos1 = queuedRunner.getPosition() - 1;
        int pos2 = queuedRunner.getPosition() - 2;
        if (pos2 >= 0) { // prevent moving to position 0
            Collections.swap(runners, pos1, pos2);
            Node runner1 = queue.getChildren().get(pos1);
            Node runner2 = queue.getChildren().get(pos2);
            queue.getChildren().set(pos1, new AnchorPane());
            queue.getChildren().set(pos2, runner1);
            queue.getChildren().set(pos1, runner2);
            runners.get(pos1).decline();
            runners.get(pos2).advance();
        }
    }

    // remove the given runner from the queue
    // move all subsequent runners one up in the queue
    public void remove(QueuedRunner queuedRunner) {
        runners.forEach(runner -> {
            if (queuedRunner.getPosition() < runner.getPosition()) runner.advance();
        });
        queue.getChildren().remove(queuedRunner.getPosition() - 1);
        runners.remove(queuedRunner);
    }

    // move the given QueuedRunner one down in the queue
    // the logical boundary conditions apply
    public void moveDown(QueuedRunner queuedRunner) {
        int pos1 = queuedRunner.getPosition() - 1;
        int pos2 = queuedRunner.getPosition();
        if (pos2 < size()) { // prevent moving past the last element in the queue
            Collections.swap(runners, pos1, pos2);
            Node runner1 = queue.getChildren().get(pos1);
            Node runner2 = queue.getChildren().get(pos2);
            queue.getChildren().set(pos1, new AnchorPane());
            queue.getChildren().set(pos2, runner1);
            queue.getChildren().set(pos1, runner2);
            runners.get(pos1).advance();
            runners.get(pos2).decline();
        }
    }

    // scale all queued runners with the given scale
    public void scale(double s) {
        runners.forEach(runner -> runner.scale(s));
    }

    public void updateRunner(String name) {
        runners.stream().filter(queuedRunner -> queuedRunner.getName().equals(name)).forEach(QueuedRunner::updateLapCount);
    }
}
