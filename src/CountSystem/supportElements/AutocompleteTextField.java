package CountSystem.supportElements;

import javafx.geometry.Side;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.util.Collection;
import java.util.function.Function;

// An extension of the normal TextField that gives autocomplete suggestions based on its current content
// very loosely based on https://gist.github.com/floralvikings/10290131
public class AutocompleteTextField extends TextField {

    // maximum size of the suggestion popup
    public static final int MAXSIZE = 10;

    // a function that returns the autocomplete suggestions based on the input of the field
    private Function<String, Collection<String>> getCompletions;

    private GoldfishContextMenu completions = new GoldfishContextMenu();

    private double menuItemTextSize = 12;

    public AutocompleteTextField(Function<String, Collection<String>> func) {
        super();
        getCompletions = func;

        // the outermost lambda expression gets converted into a changeListener
        textProperty().addListener((observableValue, s, t1) -> { // s is old value, t1 is new value
            if (!getText().equals("")) {
                completions.getItems().clear();
                // create the new suggestions with the autocomplete functionality on click/enter
                getCompletions.apply(t1).stream().map(st -> new CustomMenuItem(new Label(st), true)).forEach(item -> {
                    item.setOnAction(
                            actionEvent -> {
                                setText(((Label) ((CustomMenuItem) actionEvent.getSource()).getContent()).getText());
                                completions.hide();
                            }
                    );
                    ((Label) item.getContent()).setFont(new Font(((Label) item.getContent()).getFont().getName(), menuItemTextSize));
                    completions.getItems().add(item);
                });
                // do not show if the size is too large
                if (completions.getItems().size() < MAXSIZE)
                    completions.show(AutocompleteTextField.this, Side.BOTTOM, 0, 0);
                else {
                    completions.hide();
                }
            } else {
                completions.hide();
            }
        });

        // do not show suggestions if the text field is not in focus
        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> completions.hide());

        // autocomplete the first element of the completions list into the text field when pressing tab
        addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.TAB && completions.getItems().size() > 0) {
                setText(((Label) ((CustomMenuItem) completions.getItems().get(0)).getContent()).getText());
                completions.hide();
                keyEvent.consume(); // prevent other tab functions like changing focus to other button or textfield
            }
        });
    }

    // scale the menu items and the text field
    public void scale(double s){
        setFont(new Font(getFont().getName(), getFont().getSize() * s));
        menuItemTextSize *= s;
        // this is not strictly necessary, as the menu disappears when trying to change the size
        // completions.getItems().forEach(item -> ((Label) ((CustomMenuItem) item).getContent()).setFont(new Font(((Label) ((CustomMenuItem) item).getContent()).getFont().getName(), menuItemTextSize)));
    }

}
