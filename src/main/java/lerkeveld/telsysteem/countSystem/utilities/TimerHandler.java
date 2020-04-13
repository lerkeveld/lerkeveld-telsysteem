package lerkeveld.telsysteem.countSystem.utilities;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

// handles timing of a lap and the animation of the timer label.
public class TimerHandler implements EventHandler<ActionEvent> {
    Label label; // the label to be animated
    private int tenths = 0;

    public TimerHandler(Label label) {
        this.label = label;
    }

    // converts the tenths of a second to a time in mm:ss.t format
    public static String toText(int time) {
        return (time / 600) % 60 + ":" + ((time % 600 < 100) ? "0" : "") + (time / 10) % 60 + "." + time % 10;
    }

    @Override
    public void handle(ActionEvent event) {
        label.setText(toText(tenths++));
    }

    // return the time past in thenths of seconds
    public int getTime() {
        return tenths;
    }
}
