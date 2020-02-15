package CountSystem.Utilities;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

public class TimerHandler implements EventHandler {
    Label label;
    private int tenths = 0;

    public TimerHandler(Label label) {
        this.label = label;
    }

    public static String toText(int time) {
        return (time / 600) % 60 + ":" + ((time % 600 < 100) ? "0" : "") + (time / 10) % 60 + "." + time % 10;
    }

    @Override
    public void handle(Event event) {
        label.setText(toText(tenths++));
    }

    public int getTime() {
        return tenths;
    }
}
