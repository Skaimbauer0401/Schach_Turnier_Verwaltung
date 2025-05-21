package threem.update.schach_turnier_verwaltung.data;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeppiTest {
    // Database configuration
    private static final String DB_PATH = "DB/database";
    private static final String USER = "DBAdmin";
    private static final String PASSWORD = "DBAdmin";

    public static void main(String[] args) {
        // Get the project root directory
        String projectRoot = new File("").getAbsolutePath();
        String dbUrl = "jdbc:derby:" + projectRoot + "/" + DB_PATH + ";create=false";

        System.out.println("Connecting to database at: " + dbUrl);

        // Test database connection and retrieve a person
        Person person = getFirstPerson(dbUrl, USER, PASSWORD);

        if (person != null) {
            System.out.println("Person found:");
            System.out.println(person);

            // Print details in German as in the original code
            System.out.println("ID: " + person.getId());
            System.out.println("Username: " + person.getUsername());
            System.out.println("Passwort: " + person.getPassword());
            System.out.println("Admin: " + person.isAdmin());
            System.out.println("Siege: " + person.getWins());
            System.out.println("Niederlagen: " + person.getLosses());
            System.out.println("Unentschieden: " + person.getDraws());
        } else {
            System.out.println("Keine Person gefunden.");
        }
    }

    /**
     * Retrieves the first person from the database.
     * 
     * @param dbUrl The database URL
     * @param user The database username
     * @param password The database password
     * @return The first Person object or null if none found
     */
    public static Person getFirstPerson(String dbUrl, String user, String password) {
        String query = "SELECT * FROM persons FETCH FIRST ROW ONLY"; // Derby uses FETCH instead of LIMIT

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Establish database connection
            conn = DriverManager.getConnection(dbUrl, user, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Create a Person object from the result set
                int id = rs.getInt("personId");
                String username = rs.getString("username");
                String pass = rs.getString("password");
                boolean admin = rs.getBoolean("admin");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int draws = rs.getInt("draws");

                return new Person(id, username, pass, admin, wins, losses, draws);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            if (e.getSQLState() != null) {
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
            }
            e.printStackTrace();
        } finally {
            // Close resources in reverse order of creation
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Retrieves all persons from the database.
     * 
     * @param dbUrl The database URL
     * @param user The database username
     * @param password The database password
     * @return List of Person objects
     */
    public static List<Person> getAllPersons(String dbUrl, String user, String password) {
        String query = "SELECT * FROM persons";
        List<Person> persons = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("personId");
                String username = rs.getString("username");
                String pass = rs.getString("password");
                boolean admin = rs.getBoolean("admin");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int draws = rs.getInt("draws");

                persons.add(new Person(id, username, pass, admin, wins, losses, draws));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return persons;
    }
}
