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

public class RunDatabase {

    private CountSystem owner;
    private Connection database;
    private PreparedStatement searchRunner;
    private PreparedStatement getRunner;
    private PreparedStatement checkRunner;
    private PreparedStatement searchGroup;
    private PreparedStatement checkGroup;
    private PreparedStatement insertRunner;
    private PreparedStatement insertRunnerNoFriend;
    private PreparedStatement insertLap;

    public RunDatabase(CountSystem owner) {
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
                setupPreparedStatements();
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

    private void setupPreparedStatements() throws SQLException {
        searchRunner = database.prepareStatement("Select name from runners where name like ?");
        getRunner = database.prepareStatement("select count(), avg(time) from laps where runner=?");
        checkRunner = database.prepareStatement("Select name from runners where name=?");
        searchGroup = database.prepareStatement("Select name from groups where name like ?");
        checkGroup = database.prepareStatement("Select name from groups where name=?");
        insertRunnerNoFriend = database.prepareStatement("INSERT INTO runners (name, \"group\") values(?, ?)");
        insertRunner = database.prepareStatement("INSERT INTO runners values(?, ?, ?)");
        insertLap = database.prepareStatement("INSERT INTO laps(runner, time) values(?, ?)");

    }

    public Collection<String> searchRunners(String name) {
        try {
            ArrayList<String> items = new ArrayList<>();
            searchRunner.setString(1, "%" + name + "%");
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

    public Runner getRunner(String name) {
        // returns null if the given runner does not exist.
        if (!containsRunner(name)) return null;
        try {
            getRunner.setString(1, name);
            ResultSet rs = getRunner.executeQuery();
            return new Runner(name, rs.getInt("count()"), "Gemiddelde Tijd:", TimerHandler.toText((int) Math.round(rs.getDouble("avg(time)"))));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean containsRunner(String name) {
        try {
            checkRunner.setString(1, name);
            return !checkRunner.executeQuery().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<String> searchGroups(String name) {
        try {
            ArrayList<String> items = new ArrayList<>();
            searchGroup.setString(1, "%" + name + "%");
            ResultSet rs = searchGroup.executeQuery(); // todo sanitize
            while (rs.next()) {
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
            checkGroup.setString(1, name);
            return !checkGroup.executeQuery().isClosed(); // todo sanitize
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public void addLap(String name, int time) {
        try {
            insertLap.setString(1, name);
            insertLap.setInt(2, time);
            insertLap.executeUpdate(); //TODO sanitize input
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
