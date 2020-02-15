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

import java.util.Objects;

public class Registrator extends VBox {

    private AutocompleteTextField groupTextField;
    private AutocompleteTextField runnerTextField;
    private AutocompleteTextField friendTextField;
    private CountSystem countSystem;
    private RunDatabase database;

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
        Button selectButton = new Button("selecteer database");
        Button newButton = new Button("nieuwe database");
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

    private void processGroup(ActionEvent actionEvent) {
        if (friendTextField.getText().equals("") || database.containsRunner(friendTextField.getText())) {
            friendTextField.setStyle("");
        } else {
            friendTextField.setStyle("-fx-focus-color: #ff0000;");
            friendTextField.requestFocus();
            return;
        }
        if (database.containsGroup(groupTextField.getText())) {
            database.addRunner(runnerTextField.getText(), groupTextField.getText(), friendTextField.getText());
            countSystem.addRunnerToQueue(database.getRunner(runnerTextField.getText()));
            deactivateNewRunnerRegistration();
            groupTextField.setStyle("");
            groupTextField.clear();
            runnerTextField.clear();
            friendTextField.clear();
            runnerTextField.requestFocus();
        } else {
            groupTextField.setStyle("-fx-focus-color: #ff0000;");
        }
    }

    private void processFriend(ActionEvent actionEvent) {
        if (friendTextField.getText().equals("") || database.containsRunner(friendTextField.getText())) {
            friendTextField.setStyle("");
            groupTextField.requestFocus();
        } else friendTextField.setStyle("-fx-focus-color: #ff0000;");
    }

    private void processRunner(ActionEvent actionEvent) {
        Runner runner = database.getRunner(runnerTextField.getText());
        if (Objects.nonNull(runner)) {
            countSystem.addRunnerToQueue(runner);
            runnerTextField.clear();
        } else {
            activateNewRunnerRegistration();
        }
    }

    private void activateNewRunnerRegistration() {
        getChildren().addAll(friendTextField, groupTextField);
    }

    private void deactivateNewRunnerRegistration() {
        getChildren().removeAll(friendTextField, groupTextField);
    }

    public void changeToRunnerRegistration() {
        // basic runner registration consists of an autocomplete text field
        getChildren().setAll(runnerTextField);

    }
}
