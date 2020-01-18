import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Controller {

    @FXML
    private AnchorPane addToQueuePane;
    @FXML
    GridPane base;
    @FXML
    private VBox runnerTextPane;
    private AutocompleteTextField runnerTextBox;
    private AutocompleteTextField friendTextBox;
    private AutocompleteTextField groupTextBox;
    @FXML
    private AnchorPane runnerQueuePane;
    @FXML
    private AnchorPane previousLapsPane;
    @FXML
    private AnchorPane currentRunnerPane;
    @FXML
    private Label timer;

    private RunnerQueue runnerQueue;

    private Runner currentRunner = null;
    private PreviousLaps previousLaps;

    private Timeline timeLine = new Timeline();

    private Connection database;

    @FXML
    public void initialize() throws IOException {
        timeLine.setCycleCount(Timeline.INDEFINITE);

        previousLaps = new PreviousLaps(previousLapsPane);
        runnerQueue = new RunnerQueue(runnerQueuePane);

        runnerTextBox = new AutocompleteTextField(s -> {
            try {
                //getArray method of resultset is not supported
//                return Arrays.asList((String[]) database.createStatement().executeQuery("Select name from runners where name like \"%" + s + "%\"").getArray(0).getArray());
                ArrayList<String> items = new ArrayList<>();
                ResultSet rs = database.createStatement().executeQuery("Select name from runners where name like \"%" + s + "%\"");
                while (rs.next()){
                    items.add(rs.getString("name"));
                }
                return items;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
        runnerTextBox.setOnAction(this::addRunnerToQueue);
        runnerTextPane.getChildren().add(runnerTextBox);

        friendTextBox = new AutocompleteTextField(s -> {
            try {
                //getArray method of resultset is not supported
//                return Arrays.asList((String[]) database.createStatement().executeQuery("Select name from runners where name like \"%" + s + "%\"").getArray(0).getArray());
                ArrayList<String> items = new ArrayList<>();
                ResultSet rs = database.createStatement().executeQuery("Select name from runners where name like \"%" + s + "%\"");
                while (rs.next()){
                    items.add(rs.getString("name"));
                }
                return items;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
        friendTextBox.setPromptText("Vriend");
        friendTextBox.setOnAction(event -> groupTextBox.requestFocus());

        groupTextBox = new AutocompleteTextField(s -> {
            try {
                //getArray method of resultset is not supported
//                return Arrays.asList((String[]) database.createStatement().executeQuery("Select name from runners where name like \"%" + s + "%\"").getArray(0).getArray());
                ArrayList<String> items = new ArrayList<>();
                ResultSet rs = database.createStatement().executeQuery("Select name from groups where name like \"%" + s + "%\"");
                while (rs.next()){
                    items.add(rs.getString("name"));
                }
                return items;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
        groupTextBox.setPromptText("Groep");
        groupTextBox.setOnAction(this::addRunnerToQueue);
    }

    private void addRunnerToQueue(ActionEvent actionEvent) {
        if (!runnerTextBox.getText().equals("")) {
            try {
                String name = runnerTextBox.getText();
                if(database.createStatement().executeQuery("Select name from runners where name=\"" + name + "\"").isClosed()) { //TODO sanitize input
                    if (!runnerTextPane.getChildren().contains(friendTextBox)) {
                        runnerTextPane.getChildren().addAll(friendTextBox, groupTextBox);
                        friendTextBox.requestFocus();
                        return;
                    }
                    else if (groupTextBox.getText().equals("") || database.createStatement().executeQuery("Select name from groups where name=\"" + groupTextBox.getText() + "\"").isClosed()){ //TODO sanitize input
                        groupTextBox.requestFocus();
                        groupTextBox.selectAll();
                        groupTextBox.setStyle("-fx-focus-color: #ff0000;");
                        return;
                    }
                    else if(friendTextBox.getText().equals("")){
                        database.prepareStatement(
                                "INSERT INTO runners (name, \"group\") values(\"" + name + "\", \"" + groupTextBox.getText() + "\")").executeUpdate(); //TODO sanitize input
                    }
                    else if(database.createStatement().executeQuery("Select name from runners where name=\"" + friendTextBox.getText() + "\"").isClosed()){
                        friendTextBox.requestFocus();
                        friendTextBox.selectAll();
                        friendTextBox.setStyle("-fx-focus-color: #ff0000;");
                        return;
                    }
                    else {
                        database.prepareStatement(
                                "INSERT INTO runners values(\"" + name + "\", \"" + friendTextBox.getText() + "\", \"" + groupTextBox.getText() + "\")").executeUpdate(); //TODO sanitize input
                    }
                }
                ResultSet rs = database.createStatement().executeQuery("select count(), avg(time) from laps where runner=\"" + runnerTextBox.getText() + "\""); //TODO sanitize input
                friendTextBox.setStyle("");
                friendTextBox.clear();
                groupTextBox.setStyle("");
                groupTextBox.clear();
                runnerTextPane.getChildren().removeAll(friendTextBox, groupTextBox);
                runnerQueue.add(new Runner(name, rs.getInt("count()"), "Gemiddelde Tijd:", TimerHandler.toText((int) Math.round(rs.getDouble("avg(time)"))), timer));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        runnerTextBox.clear();
    }

    public void startNextRunner(ActionEvent actionEvent) {
        timeLine.stop();
        if (!Objects.isNull(currentRunner)){
            previousLaps.pushLap(currentRunner);
            try {
                database.prepareStatement(
                        "INSERT INTO laps (time, runner) values(\"" + currentRunner.getTime() + "\", \"" + currentRunner.getName() + "\")").executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (runnerQueue.size() > 0){
            currentRunner = runnerQueue.pop();
            currentRunner.setParent(currentRunnerPane);
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1), currentRunner.getEventHandler()));
            timeLine.playFromStart();
        }
        else {
            timer.setText("0:00.0");
            currentRunner = null;
        }

    }

    public void selectDatabase(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); //TODO should block all events in other windows, but does not seem to.
        popup.initOwner(base.getScene().getWindow());
        File file = fileChooser.showOpenDialog(popup);
        if (!Objects.isNull(file)) {

            try {
                connectToDatabase(file);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            changeAddToQueueBar();
        }
    }

    public void newDatabase(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); //TODO should block all events in other windows, but does not seem to.
        popup.initOwner(base.getScene().getWindow());
        File file = fileChooser.showSaveDialog(popup);
        if (!Objects.isNull(file)) {
            if(!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".db");
            }

            try {
                connectToDatabase(file);
                buildDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            changeAddToQueueBar();
        }
    }

    private void buildDatabase() throws SQLException {
        database.prepareStatement(
                "CREATE TABLE \"groups\" (\n" +
                "\t\"name\"\tTEXT NOT NULL UNIQUE,\n" +
                "\tPRIMARY KEY(\"name\")\n" +
                ");"
        ).executeUpdate();

        database.prepareStatement(
                "INSERT INTO \"groups\"\n" +
                        "VALUES (\"Extern\");"
        ).executeUpdate();

        database.prepareStatement(
                "CREATE TABLE \"runners\" (\n" +
                        "\t\"name\"\tTEXT NOT NULL UNIQUE,\n" +
                        "\t\"friend\"\tTEXT,\n" +
                        "\t\"group\"\tTEXT NOT NULL DEFAULT 'Extern',\n" +
                        "\tPRIMARY KEY(\"name\"),\n" +
                        "\tFOREIGN KEY(\"friend\") REFERENCES \"runners\"(\"name\")\n" +
                        "\tFOREIGN KEY(\"group\") REFERENCES \"groups\"(\"name\")\n" +
                        ");"
        ).executeUpdate();

        database.prepareStatement(
                "CREATE TABLE \"laps\" (\n" +
                        "\t\"id\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "\t\"time\"\tINTEGER NOT NULL,\n" +
                        "\t\"runner\"\tTEXT NOT NULL,\n" +
                        "\tFOREIGN KEY(\"runner\") REFERENCES \"runners\"(\"name\")\n" +
                        ");"
        ).executeUpdate();
    }

    private void connectToDatabase(File file) throws SQLException {
        database = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    private void changeAddToQueueBar() {
        Button button = new Button();
        button.setText("Voeg toe aan wachtrij");
        button.setOnAction(this::addRunnerToQueue);
        addToQueuePane.getChildren().setAll(button);
        button.prefWidthProperty().bind(addToQueuePane.widthProperty());
    }


}
