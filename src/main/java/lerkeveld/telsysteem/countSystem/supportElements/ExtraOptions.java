package lerkeveld.telsysteem.countSystem.supportElements;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExtraOptions extends Stage {

    private ToggleButton extendedInputModeToggle = new ToggleButton("Uitgebreide loper input");

    public ExtraOptions(Stage owner){
        super();
        initModality(Modality.NONE); // prevent blocking other window
        initOwner(owner);
        getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("optionsIcon.png")));
        setTitle("Extra Opties");
        setAlwaysOnTop(true);

        // setup layout scene
        VBox vBox = new VBox(10);
        vBox.paddingProperty().set(new Insets(10, 10, 10, 10));
        HBox hBox = new HBox(10);
        vBox.getChildren().add(hBox);
        hBox.prefWidthProperty().bind(vBox.widthProperty());

        // setup buttons to enable edits to the database
        // todo change buttons togglebuttons, part of a toggle group.
        VBox buttons = new VBox(10);
        buttons.prefWidthProperty().bind(hBox.widthProperty().subtract(20).divide(2));
        Button addGroupsButton = new Button("Voeg Groepen Toe");
        Button changeNameButton = new Button("Pas Naam Aan");
        Button changeGroupButton = new Button("Pas Groep Aan");
        addGroupsButton.setOnAction(actionEvent -> activateAddGroups());
        changeNameButton.setOnAction(actionEvent -> activateChangeName());
        changeGroupButton.setOnAction(actionEvent -> activateChangeGroup());
        buttons.getChildren().addAll(addGroupsButton, changeNameButton, changeGroupButton);
        buttons.getChildren().forEach(child -> ((Button) child).prefWidthProperty().bind(buttons.widthProperty().subtract(30)));

        // setup settings toggle switches
        VBox settings = new VBox(10);
        settings.prefWidthProperty().bind(hBox.widthProperty().subtract(20).divide(2));
        settings.getChildren().addAll(extendedInputModeToggle);

        hBox.getChildren().addAll(buttons, settings);
        
        setScene(new Scene(vBox, 700, 500));
    }

    private void activateChangeGroup() {
        return;
    }

    private void activateChangeName() {
        return;
    }

    private void activateAddGroups() {
        return;
    }

    public boolean isExtendedInputModeSet(){
        return extendedInputModeToggle.isSelected();
    }
}