package CountSystem.Utilities;

import CountSystem.CountSystem;
import CountSystem.Utilities.TimerHandler;
import CountSystem.supportElements.Runner;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class RunDatabase {

    private CountSystem owner;
    private Connection database;

    public RunDatabase(CountSystem owner){
        super();
        this.owner = owner;
    }

    public void selectDatabase() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); //TODO should block all events in other windows, but does not seem to.
        popup.initOwner(owner.getWindow());
        File file = fileChooser.showOpenDialog(popup);
        if (!Objects.isNull(file)) {

            try {
                connectToDatabase(file);
                owner.changeToRunnerRegistration();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void newDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); //TODO should block all events in other windows, but does not seem to.
        popup.initOwner(owner.getWindow());
        File file = fileChooser.showSaveDialog(popup);
        if (!Objects.isNull(file)) {
            if(!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".db");
            }

            try {
                connectToDatabase(file);
                buildDatabase();
                owner.changeToRunnerRegistration();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
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

    public Collection<String> searchRunners(String name) {
        try {
                ArrayList<String> items = new ArrayList<>();
                ResultSet rs = database.createStatement().executeQuery("Select name from runners where name like \"%" + name + "%\""); // todo sanitize input
                while (rs.next()){
                    items.add(rs.getString("name"));
                }
                return items;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
    }

    public Runner getRunner(String name) {
        // returns null if the given runner does not exist.
        if (!containsRunner(name)) return null;
        try {
            ResultSet rs = database.createStatement().executeQuery("select count(), avg(time) from laps where runner=\"" + name + "\""); //TODO sanitize input
            return new Runner(name, rs.getInt("count()"), "Gemiddelde Tijd:", TimerHandler.toText((int) Math.round(rs.getDouble("avg(time)"))));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean containsRunner(String name) {
        try {
            return !database.createStatement().executeQuery("Select name from runners where name=\"" + name + "\"").isClosed(); // todo sanitize
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<String> searchGroups(String name) {
                    try {
                ArrayList<String> items = new ArrayList<>();
                ResultSet rs = database.createStatement().executeQuery("Select name from groups where name like \"%" + name + "%\""); // todo sanitize
                while (rs.next()){
                    items.add(rs.getString("name"));
                }
                return items;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
    }

    public boolean containsGroup(String name) {
        try {
            return !database.createStatement().executeQuery("Select name from groups where name=\"" + name + "\"").isClosed(); // todo sanitize
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addRunner(String name, String group, String friend) {
        if (friend.equals("")){
            try {
                database.prepareStatement(
                                    "INSERT INTO runners (name, \"group\") values(\"" + name + "\", \"" + group + "\")").executeUpdate(); //TODO sanitize input
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                database.prepareStatement(
                                   "INSERT INTO runners values(\"" + name + "\", \"" + friend + "\", \"" + group + "\")").executeUpdate(); //TODO sanitize input
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addLap(String name, int time) {
        try {
            database.prepareStatement(
                    "INSERT INTO laps(runner, time) values(\"" + name + "\", " + Integer.toString(time) + ")").executeUpdate(); //TODO sanitize input
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
