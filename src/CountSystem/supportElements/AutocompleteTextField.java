package CountSystem.supportElements;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.function.Function;

// An extension of the normal TextField that gives autocomplete suggestions based on its current content
// very loosely based on https://gist.github.com/floralvikings/10290131
public class AutocompleteTextField extends TextField {

    // maximum size of the suggestion popup
    public static final int MAXSIZE = 10;

    // a function that returns the autocomplete suggestions based on the input of the field
    private Function<String, Collection<String>> getCompletions;

    private ContextMenu completions = new ContextMenu();

    public AutocompleteTextField(Function<String, Collection<String>> func) {
        super();
        getCompletions = func;

        // the outermost lambda expression gets converted into a changeListener
        textProperty().addListener((observableValue, s, t1) -> { // s is old value, t1 is new value
            if (!getText().equals("")) {
                completions.getItems().clear(); // clear the current suggestions
                // create the new suggestions with the autocomplete functionality on click/enter
                getCompletions.apply(t1).stream().map(st -> new CustomMenuItem(new Label(st), true)).forEach(item -> {
                    item.setOnAction(
                            actionEvent -> {
                                setText(((Label) ((CustomMenuItem) actionEvent.getSource()).getContent()).getText());
                                completions.hide();
                            }
                    );
                    completions.getItems().add(item);
                });
                // do not show if the size is too large
                if (completions.getItems().size() < MAXSIZE)
                    completions.show(AutocompleteTextField.this, Side.BOTTOM, 0, 0);
                else completions.hide();
            } else completions.hide();
        });

        // do not show suggestions if the text field is not in focus
        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> completions.hide());
    }


}
