package lerkeveld.telsysteem.countSystem.mainElements;

import lerkeveld.telsysteem.countSystem.CountSystem;
import lerkeveld.telsysteem.countSystem.utilities.RunDatabase;
import lerkeveld.telsysteem.countSystem.supportElements.AutocompleteTextField;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

// adds interface for selecting the database
// adds interface for registering runners
public class Registrator extends VBox {

    private AutocompleteTextField runnerTextField;
    private AutocompleteTextField friendTextField;
    private CountSystem countSystem;
    private RunDatabase database;
    private Button newButton;
    private Button selectButton;
    private boolean registrationActive = false;

    public Registrator(CountSystem countSystem, RunDatabase database) {
        super();
        this.countSystem = countSystem;
        this.database = database;

        // layout of the VBox
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 5;");
        setSpacing(10);

        // starts out with two buttons in a HBox, to get a database
        HBox hbox = new HBox();
        getChildren().add(hbox);
        hbox.prefWidthProperty().bind(widthProperty().subtract(20));
        selectButton = new Button("selecteer database");
        newButton = new Button("nieuwe database");
        selectButton.setOnAction(actionEvent -> database.selectDatabase());
        newButton.setOnAction(actionEvent -> database.newDatabase());
        selectButton.prefWidthProperty().bind(hbox.widthProperty().divide(2));
        newButton.prefWidthProperty().bind(hbox.widthProperty().divide(2));
        hbox.getChildren().setAll(selectButton, newButton);

        // later, an AutocompleteTextField will be used to add runners
        runnerTextField = new AutocompleteTextField(database::searchRunners);
        runnerTextField.setOnAction(this::processRunner);
        runnerTextField.setPromptText("voeg loper toe");

        // new runner can be added with one extra AutocompleteTextFields
        friendTextField = new AutocompleteTextField(database::searchRunners);
        friendTextField.setOnAction(this::processFriend);
        friendTextField.setPromptText("vriend");
    }

    // check if friend exist, if not, make the text field red, else add new runner
    private void processFriend(ActionEvent actionEvent) {
        if (database.containsRunner(runnerTextField.getText())) {
            processRunner(actionEvent);
            return;
        }
        if (friendTextField.getText().equals("") || database.containsRunner(friendTextField.getText())) {
            // setup new runner
            database.addRunner(runnerTextField.getText(), friendTextField.getText());
            processRunner(actionEvent);
            // clear all registration fields
            deactivateNewRunnerRegistration();
            runnerTextField.requestFocus();
        } else friendTextField.setStyle("-fx-focus-color: #ff0000;"); // indicate non-existent runner
    }

    // setup a new runner if it is already in the database, else start the new runner setup
    private void processRunner(ActionEvent actionEvent) {
        // don't do anything if the textfield is empty
        if (!runnerTextField.getText().equals("")){
            if (database.containsRunner(runnerTextField.getText())) {
                countSystem.addRunnerToQueue(database.getRunner(runnerTextField.getText()));
                runnerTextField.clear();
                deactivateNewRunnerRegistration(); // if the registration was activated by a typo
            } else {
                activateNewRunnerRegistration();
                friendTextField.requestFocus();
            }
        }
    }

    // start the registration of a new runner
    private void activateNewRunnerRegistration() {
        if (!registrationActive) {
            getChildren().addAll(friendTextField);
            registrationActive = true;
        }
    }

    // end the registration of a new runner
    private void deactivateNewRunnerRegistration() {
        if (registrationActive){
            getChildren().removeAll(friendTextField);
            friendTextField.setStyle("");
            runnerTextField.clear();
            friendTextField.clear();
            registrationActive = false;
        }

    }

    // switch from database selection to runner registration mode
    public void changeToRunnerRegistration() {
        // basic runner registration consists of an autocomplete text field
        getChildren().setAll(runnerTextField);

    }

    // scale all elements of this registrator with the given scale s
    public void scale(double s) {
        newButton.setFont(new Font(newButton.getFont().getName(), newButton.getFont().getSize() * s));
        selectButton.setFont(new Font(selectButton.getFont().getName(), selectButton.getFont().getSize() * s));
        runnerTextField.scale(s);
        friendTextField.scale(s);
    }
}
