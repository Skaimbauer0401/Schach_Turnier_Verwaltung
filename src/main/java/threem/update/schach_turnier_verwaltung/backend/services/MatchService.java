package threem.update.schach_turnier_verwaltung.backend.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class MatchService {

    public String addMatches(int tournamentId, Map<String, String> matchData){
        try {
            Connection con = databaseConnection();

            PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM matches WHERE tournamentId = ? AND player1Id = ? AND player2Id = ?");

            PreparedStatement insertStmt = con.prepareStatement("INSERT INTO matches (tournamentId, player1Id, player2Id, result) VALUES (?, ?, ?, ?)");

            int successCount = 0;

            for (Map.Entry<String, String> entry : matchData.entrySet()) {
                String key = entry.getKey();
                String result = entry.getValue();

                String[] players = key.split("-");
                if (players.length != 2) {
                    continue;
                }

                try {
                    int player1Id = Integer.parseInt(players[0]);
                    int player2Id = Integer.parseInt(players[1]);

                    deleteStmt.setInt(1, tournamentId);
                    deleteStmt.setInt(2, player1Id);
                    deleteStmt.setInt(3, player2Id);
                    deleteStmt.executeUpdate();

                    if (result.equals("N/A")) {
                        result = "N";
                    }

                    insertStmt.setInt(1, tournamentId);
                    insertStmt.setInt(2, player1Id);
                    insertStmt.setInt(3, player2Id);
                    insertStmt.setString(4, result);

                    successCount += insertStmt.executeUpdate();
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            deleteStmt.close();
            insertStmt.close();
            con.close();

            return "Successfully saved " + successCount + " match results";
        } catch (SQLException e) {
            return "SQL Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public Map<String, String> getMatches(int tournamentId) {
        Map<String, String> matchResults = new HashMap<>();
        try {
            Connection con = databaseConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT player1Id, player2Id, result FROM matches WHERE tournamentId = ?");
            stmt.setInt(1, tournamentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int player1Id = rs.getInt("player1Id");
                int player2Id = rs.getInt("player2Id");
                String result = rs.getString("result");

                if (result.equals("N")) {
                    result = "N/A";
                }

                matchResults.put(player1Id + "-" + player2Id, result);
            }

            rs.close();
            stmt.close();
            con.close();

            return matchResults;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public Connection databaseConnection() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/threem/update/schach_turnier_verwaltung/backend/database_important/database_connection"));
            String line = br.readLine();
            String url = line.split(";")[1];
            line = br.readLine();
            String username = line.split(";")[1];
            line = br.readLine();
            String dbpassword = line.split(";")[1];
            br.close();

            return DriverManager.getConnection(url, username, dbpassword);
        } catch (SQLException | IOException e) {
            System.out.println("DB Connection failed");
            return null;
        }
    }
}
