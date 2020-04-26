package lerkeveld.telsysteem.countSystem.supportElements;

import lerkeveld.telsysteem.countSystem.CountSystem;
import lerkeveld.telsysteem.countSystem.utilities.RunDatabase;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

// adds interface for selecting the database
// adds interface for registering runners
public class Registrator extends VBox {

    private AutocompleteTextField runnerTextField;
    private AutocompleteTextField friendTextField;
    private CountSystem countSystem;
    private RunDatabase database;
    private boolean registrationActive = false;

    public Registrator(CountSystem countSystem, RunDatabase database) {
        super();
        this.countSystem = countSystem;
        this.database = database;

        // layout of the VBox
        setAlignment(Pos.TOP_CENTER);
        setSpacing(10);

        // an AutocompleteTextField will be used to add runners
        runnerTextField = new AutocompleteTextField(database::searchRunners);
        runnerTextField.setOnAction(this::processRunner);
        runnerTextField.setPromptText("voeg loper toe");

        // new runner can be added with one extra AutocompleteTextFields
        friendTextField = new AutocompleteTextField(database::searchRunners);
        friendTextField.setOnAction(this::processFriend);
        friendTextField.setPromptText("vriend");

        // populate vBox
        getChildren().setAll(runnerTextField);
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

    // scale all elements of this registrator with the given scale s
    public void scale(double s) {
        runnerTextField.scale(s);
        friendTextField.scale(s);
    }
}
