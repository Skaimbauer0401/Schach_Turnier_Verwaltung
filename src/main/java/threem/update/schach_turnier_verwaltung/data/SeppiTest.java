package threem.update.schach_turnier_verwaltung.data;

import java.sql.*;

public class SeppiTest {
    public static void main(String[] args) {
        String url = "jdbc:derby:C:/Users/Sebastian/Desktop/HTL/Schach_GIT/DB/database;create=false";
        String user = "DBAdmin";
        String password = "DBAdmin";

        String query = "SELECT * FROM persons FETCH FIRST ROW ONLY"; // Derby verwendet FETCH statt LIMIT

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int id = rs.getInt("personId");
                String username = rs.getString("username");
                String pass = rs.getString("password");
                boolean admin = rs.getBoolean("admin");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int draws = rs.getInt("draws");

                System.out.println("Person:");
                System.out.println("ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("Passwort: " + pass);
                System.out.println("Admin: " + admin);
                System.out.println("Siege: " + wins);
                System.out.println("Niederlagen: " + losses);
                System.out.println("Unentschieden: " + draws);
            } else {
                System.out.println("Keine Person gefunden.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
