import CountSystem.CountSystem;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // create and show the main scene of this application
        primaryStage.setScene(new CountSystem());
        primaryStage.setWidth(1080);
        primaryStage.setHeight(720);
        primaryStage.show();
    }
}
