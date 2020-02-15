package CountSystem.MainElements;

import CountSystem.supportElements.QueuedRunner;
import CountSystem.supportElements.Runner;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.LinkedList;

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


    public void add(Runner runner) {
        QueuedRunner queuedRunner = new QueuedRunner(runner, this);
        runners.add(queuedRunner);
        queue.getChildren().add(queuedRunner);
    }

    public int size() {
        return runners.size();
    }

    public Runner pop() {
        // returns null if no runner is present
        if (size() == 0) return null;
        queue.getChildren().remove(0);
        runners.forEach(QueuedRunner::advance);
        return runners.pop().getRunner();
    }

    public void moveUp(QueuedRunner queuedRunner) {
        int pos1 = queuedRunner.getPosition() - 1;
        int pos2 = queuedRunner.getPosition() - 2;
        if (pos2 >= 0) {
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

    public void remove(QueuedRunner queuedRunner) {
        runners.forEach(runner -> {
            if (queuedRunner.getPosition() < runner.getPosition()) runner.advance();
        });
        queue.getChildren().remove(queuedRunner.getPosition() - 1);
        runners.remove(queuedRunner);
    }

    public void moveDown(QueuedRunner queuedRunner) {
        int pos1 = queuedRunner.getPosition() - 1;
        int pos2 = queuedRunner.getPosition();
        if (pos2 < size()) {
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
}
