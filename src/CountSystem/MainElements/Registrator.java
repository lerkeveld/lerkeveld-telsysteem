package CountSystem.MainElements;

import CountSystem.CountSystem;
import CountSystem.Utilities.RunDatabase;
import CountSystem.supportElements.AutocompleteTextField;
import CountSystem.supportElements.Runner;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Objects;

// adds interface for selecting the database
// adds interface for registering runners
public class Registrator extends VBox {

    private AutocompleteTextField groupTextField;
    private AutocompleteTextField runnerTextField;
    private AutocompleteTextField friendTextField;
    private CountSystem countSystem;
    private RunDatabase database;
    private Button newButton;
    private Button selectButton;

    public Registrator(CountSystem countSystem, RunDatabase database) {
        super();
        this.countSystem = countSystem;
        this.database = database;

        // layout of the VBox
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 10;");
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
        runnerTextField = new AutocompleteTextField(s -> database.searchRunners(s));
        runnerTextField.setOnAction(this::processRunner);
        runnerTextField.setPromptText("voeg loper toe");

        // new runner can be added with two other AutocompleteTextFields
        groupTextField = new AutocompleteTextField(database::searchGroups);
        friendTextField = new AutocompleteTextField(database::searchRunners);
        groupTextField.setOnAction(this::processGroup);
        friendTextField.setOnAction(this::processFriend);
        groupTextField.setPromptText("group");
        friendTextField.setPromptText("vriend");
    }

    // make sure the group field is correctly filled in on action of this field
    private void processGroup(ActionEvent actionEvent) {
        // give the friend text field a red accent to indicate a non-existing runner, if the runner does not exist
        if (friendTextField.getText().equals("") || database.containsRunner(friendTextField.getText())) {
            friendTextField.setStyle("");
        } else {
            friendTextField.setStyle("-fx-focus-color: #ff0000;");
            friendTextField.requestFocus();
            return;
        }
        if (database.containsGroup(groupTextField.getText())) {
            // setup new runner
            database.addRunner(runnerTextField.getText(), groupTextField.getText(), friendTextField.getText());
            countSystem.addRunnerToQueue(database.getRunner(runnerTextField.getText()));
            // clear all registration fields
            deactivateNewRunnerRegistration();
            groupTextField.setStyle("");
            groupTextField.clear();
            runnerTextField.clear();
            friendTextField.clear();
            runnerTextField.requestFocus();
        } else {
            groupTextField.setStyle("-fx-focus-color: #ff0000;"); // red accent if group does not exist
        }
    }

    // check if friend exist, if not, make the text field red, else move on to group text field
    private void processFriend(ActionEvent actionEvent) {
        if (friendTextField.getText().equals("") || database.containsRunner(friendTextField.getText())) {
            friendTextField.setStyle("");
            groupTextField.requestFocus();
        } else friendTextField.setStyle("-fx-focus-color: #ff0000;");
    }

    // setup a new runner if it is already in the database, else start the new runner setup
    private void processRunner(ActionEvent actionEvent) {
        Runner runner = database.getRunner(runnerTextField.getText());
        if (Objects.nonNull(runner)) {
            countSystem.addRunnerToQueue(runner);
            runnerTextField.clear();
        } else {
            activateNewRunnerRegistration();
        }
    }

    // start the registration of a new runner
    private void activateNewRunnerRegistration() {
        getChildren().addAll(friendTextField, groupTextField);
    }

    // end the registration of a new runner
    private void deactivateNewRunnerRegistration() {
        getChildren().removeAll(friendTextField, groupTextField);
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
        groupTextField.scale(s);
        runnerTextField.scale(s);
        friendTextField.scale(s);
    }
}
