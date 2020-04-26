package lerkeveld.telsysteem.countSystem.supportElements;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExtraOptions extends Stage {
    public ExtraOptions(Stage owner){
        super();
        initModality(Modality.NONE); // prevent blocking other window
        initOwner(owner);
        getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("optionsIcon.png")));
        setTitle("Extra Opties");

        // test scene
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This is a Dialog"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        setScene(dialogScene);
    }
}