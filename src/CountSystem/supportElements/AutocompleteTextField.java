package CountSystem.supportElements;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.function.Function;

// based on https://gist.github.com/floralvikings/10290131
public class AutocompleteTextField extends TextField {

    public static final int MAXSIZE = 10;

    private Function<String, Collection<String>> getCompletions;

    private ContextMenu completions = new ContextMenu();

    public AutocompleteTextField(Function<String, Collection<String>> func) {
        super();
        getCompletions = func;

        textProperty().addListener((observableValue, s, t1) -> { // s is old value, t1 is new value
            if (!getText().equals("")) {
                completions.getItems().clear();
                getCompletions.apply(t1).stream().map(st -> new CustomMenuItem(new Label(st), true)).forEach(item -> {
                    item.setOnAction(
                            actionEvent -> {
                                setText(((Label) ((CustomMenuItem) actionEvent.getSource()).getContent()).getText());
                                completions.hide();
                            }
                    );
                    completions.getItems().add(item);
                });
                if (completions.getItems().size() < MAXSIZE)
                    completions.show(AutocompleteTextField.this, Side.BOTTOM, 0, 0);
                else completions.hide();
            } else completions.hide();
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> completions.hide());
    }


}
