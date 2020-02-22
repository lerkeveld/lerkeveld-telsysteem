package CountSystem.Utilities;

import CountSystem.CountSystem;
import CountSystem.supportElements.Runner;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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

    public RunDatabase(CountSystem owner) {
        super();
        // necessary for the file selection popup windows
        this.owner = owner;
    }

    // select an existing database, to be used by the application
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
                setupPreparedStatements();
                owner.changeToRunnerRegistration();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // create new database, to be used by the application, and save it in the user defined location
    public void newDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*SQLite database", "*.db"));
        fileChooser.setTitle("Database chooser");
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); //TODO should block all events in other windows, but does not seem to.
        popup.initOwner(owner.getWindow());
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
    }

    // setup the main tables and elements of the database
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

    // setup connection to database stored in the given file
    private void connectToDatabase(File file) throws SQLException {
        database = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
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
    public Runner getRunner(String name) throws SQLException {
        // returns null if the given runner does not exist.
        getRunner.setString(1, name);
        getRunnerName.setString(1, name);
        ResultSet rs = getRunner.executeQuery();
        return new Runner(getRunnerName.executeQuery().getString("name"), rs.getInt("count()"), "Gemiddelde Tijd:", TimerHandler.toText((int) Math.round(rs.getDouble("avg(time)"))), this);
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
