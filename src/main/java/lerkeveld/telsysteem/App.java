package lerkeveld.telsysteem;

import lerkeveld.telsysteem.countSystem.CountSystem;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // create and show the main scene of this application
        primaryStage.setScene(new CountSystem());
        primaryStage.setWidth(1080);
        primaryStage.setHeight(720);
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("mainIcon.png")));
        primaryStage.setTitle("Lerkeveld Telsysteem 24-uren loop");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
