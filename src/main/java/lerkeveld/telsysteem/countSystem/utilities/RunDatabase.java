package lerkeveld.telsysteem.countSystem.utilities;

import lerkeveld.telsysteem.countSystem.CountSystem;
import lerkeveld.telsysteem.countSystem.supportElements.Runner;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

// takes care of all transaction with the SQLite database
public class RunDatabase {

    private CountSystem owner;
    private Connection database;
    private PreparedStatement searchRunner;
    private PreparedStatement getRunnerName;
    private PreparedStatement getRunner;
    private PreparedStatement checkRunner;
    private PreparedStatement searchGroup;
    private PreparedStatement checkGroup;
    private PreparedStatement insertRunner;
    private PreparedStatement insertRunnerNoFriend;
    private PreparedStatement insertLap;
    private PreparedStatement getLaps;
    private volatile boolean popupOn = false;

    public RunDatabase(CountSystem owner) {
        super();
        // necessary for the file selection popup windows
        this.owner = owner;
    }

    // select an existing database, to be used by the application
    public void selectDatabase() {
        if (popupOn) return; // prevent multiple selection windows
        popupOn = true;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        File file = fileChooser.showOpenDialog(popup);
        if (!Objects.isNull(file)) {
            try {
                connectToDatabase(file);
                if (checkDatabase()){
                    setupPreparedStatements();
                    owner.changeToRunnerRegistration();
                }
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("De gegeven database heeft niet de correcte structuur!");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        popupOn = false;
    }

    // create new database, to be used by the application, and save it in the user
    // defined location
    public void newDatabase() {
        if (popupOn) return; // prevent multiple selection windows
        popupOn = true;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        File file = fileChooser.showSaveDialog(popup);
        if (!Objects.isNull(file)) {
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".db");
            }

            try {
                connectToDatabase(file);
                buildDatabase();
                setupPreparedStatements();
                owner.changeToRunnerRegistration();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        popupOn = false;
    }

    // setup the main tables and elements of the database
    private void buildDatabase() throws SQLException {
        database.prepareStatement(
                "CREATE TABLE groups (name TEXT NOT NULL UNIQUE, PRIMARY KEY(name));"
        ).executeUpdate();

        database.prepareStatement(
                "INSERT INTO groups VALUES ('Extern');"
        ).executeUpdate();

        database.prepareStatement(
                "CREATE TABLE runners (name TEXT NOT NULL UNIQUE, friend TEXT, \"group\" TEXT NOT NULL DEFAULT 'Extern', PRIMARY KEY(name), FOREIGN KEY(friend) REFERENCES runners(name) FOREIGN KEY(\"group\") REFERENCES groups(name));"
        ).executeUpdate();

        database.prepareStatement(
                "CREATE TABLE laps (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, time INTEGER NOT NULL, runner TEXT NOT NULL, FOREIGN KEY(runner) REFERENCES runners(name));"
        ).executeUpdate();
    }

    // setup connection to database stored in the given file
    private void connectToDatabase(File file) throws SQLException {
        database = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    // returns whether the database has the correct structure
    // this is a basic check
    private boolean checkDatabase(){
        try{
            // check laps table structure
            ResultSet rs = database.prepareStatement("pragma table_info('laps')").executeQuery();
            rs.next(); // moves the cursor to the first row, even though it is already at the first row
            if (!rs.getString("name").equals("id")) return false;
            rs.next(); // moves the cursor to the next row
            if (!rs.getString("name").equals("time")) return false;
            rs.next();
            if (!rs.getString("name").equals("runner")) return false;
            // check runners table structure
            rs = database.prepareStatement("pragma table_info('runners')").executeQuery();
            rs.next();
            if (!rs.getString("name").equals("name")) return false;
            rs.next();
            if (!rs.getString("name").equals("friend")) return false;
            rs.next();
            if (!rs.getString("name").equals("group")) return false;
            // check groups table structure
            rs = database.prepareStatement("pragma table_info('groups')").executeQuery();
            rs.next();
            if (!rs.getString("name").equals("name")) return false;
            
            // all checks cleared
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }

    // setup the different prepared statements that require user input
    // benefits from increased performance and no risk of injection attacks
    private void setupPreparedStatements() throws SQLException {
        searchRunner = database.prepareStatement("Select name from runners where name like ?");
        getRunnerName = database.prepareStatement("select name from runners where name like ?");
        getRunner = database.prepareStatement("select count(), avg(time) from laps where runner like ?");
        checkRunner = database.prepareStatement("Select name from runners where name like ?");
        searchGroup = database.prepareStatement("Select name from groups where name like ?");
        checkGroup = database.prepareStatement("Select name from groups where name like ?");
        insertRunnerNoFriend = database.prepareStatement("INSERT INTO runners (name, \"group\") values(?, ?)");
        insertRunner = database.prepareStatement("INSERT INTO runners values(?, ?, ?)");
        insertLap = database.prepareStatement("INSERT INTO laps(runner, time) values(?, ?)");
        getLaps = database.prepareStatement("select count() from laps where runner like ?");
    }

    // get all runner names with the given string in their name
    public Collection<String> searchRunners(String name) {
        try {
            ArrayList<String> items = new ArrayList<>();
            searchRunner.setString(1, name + "%");
            ResultSet rs = searchRunner.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("name"));
            }
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // get the lap count of the given runner
    public int getLapCount(String name){
        try {
            getLaps.setString(1, name);
            return getLaps.executeQuery().getInt("count()");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // get a Runner object based on the given name
    public Runner getRunner(String name){
        try {
            getRunner.setString(1, name);
            getRunnerName.setString(1, name);
            ResultSet rs = getRunner.executeQuery();
            return new Runner(getRunnerName.executeQuery().getString("name"), rs.getInt("count()"), "Gemiddelde Tijd:", TimerHandler.toText((int) Math.round(rs.getDouble("avg(time)"))), this);
        } catch (SQLException e) {
            return null;
        }
    }

    // check if the given runner name is in the database
    public boolean containsRunner(String name) {
        try {
            checkRunner.setString(1, name);
            return !checkRunner.executeQuery().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // get all group names with the given string in their name
    public Collection<String> searchGroups(String name) {
        try {
            ArrayList<String> items = new ArrayList<>();
            searchGroup.setString(1, name + "%");
            ResultSet rs = searchGroup.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("name"));
            }
            return items;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // check if the given group name is in the database
    public boolean containsGroup(String name) {
        try {
            checkGroup.setString(1, name);
            return !checkGroup.executeQuery().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // create a new runner in the database with the given name, group and friend
    public void addRunner(String name, String group, String friend) {
        if (friend.equals("")) {
            try {
                insertRunnerNoFriend.setString(1, name);
                insertRunnerNoFriend.setString(2, group);
                insertRunnerNoFriend.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                insertRunner.setString(1, name);
                insertRunner.setString(2, friend);
                insertRunner.setString(3, group);
                insertRunner.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // create a new lap in the database for the given runner and given time
    public void addLap(String name, int time) {
        try {
            insertLap.setString(1, name);
            insertLap.setInt(2, time);
            insertLap.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
