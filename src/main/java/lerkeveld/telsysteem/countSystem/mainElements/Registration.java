package lerkeveld.telsysteem.countSystem.mainElements;

import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lerkeveld.telsysteem.countSystem.CountSystem;
import lerkeveld.telsysteem.countSystem.supportElements.ExtraOptions;
import lerkeveld.telsysteem.countSystem.supportElements.Registrator;
import lerkeveld.telsysteem.countSystem.utilities.RunDatabase;

public class Registration extends VBox {
    private Registrator registrator;
    private Button extraOptionsButton;
    private Button newButton;
    private Button selectButton;
    private ExtraOptions extraOptions = null;

    public Registration(CountSystem countSystem, RunDatabase database){
        super();
        registrator = new Registrator(countSystem, database);
        extraOptionsButton = new Button("Extra Opties");
        extraOptionsButton.setOnAction(actionEvent -> popupExtraOptions());

        // VBox Layout
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-border-color: Gainsboro; -fx-border-insets: 0; -fx-border-width: 2; -fx-border-radius: 5;");

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

        // bind size of registrator
        registrator.prefWidthProperty().bind(widthProperty().subtract(20));
        registrator.prefHeightProperty().bind(heightProperty().subtract(10).subtract(extraOptionsButton.heightProperty()));

        // populate VBox
        getChildren().setAll(hbox);
    }

    private void popupExtraOptions() {
        // roundabout way to set the owner of the popup, as is it not defined on creation of this registrator
        if (Objects.isNull(extraOptions)){
            extraOptions = new ExtraOptions((Stage) getScene().getWindow());
        }
        extraOptions.show();
    }

    // switch from database selection to runner registration mode
    public void changeToRunnerRegistration() {
        setAlignment(Pos.BOTTOM_RIGHT);
        getChildren().setAll(registrator, extraOptionsButton);
    }
    

    public void scale(double s) {
        newButton.setFont(new Font(newButton.getFont().getName(), newButton.getFont().getSize() * s));
        selectButton.setFont(new Font(selectButton.getFont().getName(), selectButton.getFont().getSize() * s));
        registrator.scale(s);
    }
}