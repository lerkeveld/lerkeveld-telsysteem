import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.LinkedList;

public class RunnerQueue {
    private VBox queue = new VBox();
    private LinkedList<QueuedRunner> runners = new LinkedList<>();

    public RunnerQueue(Pane parent) {
        ScrollPane scrollPane = new ScrollPane();
        parent.getChildren().setAll(scrollPane);
        scrollPane.prefHeightProperty().bind(parent.heightProperty());
        scrollPane.prefWidthProperty().bind(parent.widthProperty());
        scrollPane.setFitToWidth(true);

        queue.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(queue);
    }


    public void add(Runner runner) {
        QueuedRunner queuedRunner = new QueuedRunner(runner, this);
        runners.add(queuedRunner);
        queuedRunner.addToParent(queue);
        runner.scale(0.8);
    }

    public int size() {
        return runners.size();
    }

    public Runner pop() {
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
