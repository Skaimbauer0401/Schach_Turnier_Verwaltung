package threem.update.schach_turnier_verwaltung.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;

import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
public class TournamentController {

    private String url;
    private String user = "DBAdmin";
    private String dbpassword = "DBAdmin";

    @GetMapping("/tournaments/newtournament/{tournament_name}/{start_time}/{end_time}")
    public String newTournament(@PathVariable String tournament_name, @PathVariable String start_time, @PathVariable String end_time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date parsedStartDate = dateFormat.parse(start_time);
        Date parsedEndDate = dateFormat.parse(end_time);

        Timestamp start = new Timestamp(parsedStartDate.getTime());
        Timestamp end = new Timestamp(parsedEndDate.getTime());

        Tournament tournament = new Tournament(1, "Tournament", start, end);

        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();

        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO tournaments (name, start_time, end_time) VALUES (?, ?, ?)");
            pstmt.setString(1, tournament_name);
            pstmt.setTimestamp(2, start);
            pstmt.setTimestamp(3, end);
            int i = pstmt.executeUpdate();
            con.close();
            return String.valueOf(i);
        } catch (SQLException e) {
            return "SQL Fehler";
        }
    }

    @GetMapping("/tournaments/tournament/alter/{tournamentId}/{name}/{start_time}/{end_time}")
    public String alterTournament(@PathVariable int tournamentId, @PathVariable String name, @PathVariable long start_time, @PathVariable long end_time) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();
        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);
            PreparedStatement pstmt = con.prepareStatement("UPDATE tournaments SET name = ?, start_time = ?, end_time = ? WHERE tournamentId = ?");
            pstmt.setString(1, name);
            pstmt.setTimestamp(2, new Timestamp(start_time));
            pstmt.setTimestamp(3, new Timestamp(end_time));
            pstmt.setInt(4, tournamentId);
            int i = pstmt.executeUpdate();
            con.close();

            return String.valueOf(i);
        } catch (SQLException e) {
            return "SQL Fehler: " + e.getMessage();
        } catch (Exception e) {
            return "Unbekannter Fehler: " + e.getMessage();
        }
    }

    @GetMapping("/tournaments/tournament/delete/{tournamentId}")
    public String deleteTournament(@PathVariable int tournamentId) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();
        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            // First delete any references in the persons_tournaments table
            PreparedStatement pstmtRefs = con.prepareStatement("DELETE FROM persons_tournaments WHERE tournamentId = ?");
            pstmtRefs.setInt(1, tournamentId);
            pstmtRefs.executeUpdate();

            // Then delete the tournament
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM tournaments WHERE tournamentId = ?");
            pstmt.setInt(1, tournamentId);
            int i = pstmt.executeUpdate();
            con.close();

            return String.valueOf(i);
        } catch (SQLException e) {
            return "SQL Fehler: " + e.getMessage();
        } catch (Exception e) {
            return "Unbekannter Fehler: " + e.getMessage();
        }
    }

    @PostMapping("/tournaments/addMatches/{tournamentId}")
    public String addMatches(@PathVariable int tournamentId, @RequestBody Map<String, String> matchData) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();

        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            // Prepare statements for deleting and inserting matches
            PreparedStatement deleteStmt = con.prepareStatement(
                "DELETE FROM matches WHERE tournamentId = ? AND player1Id = ? AND player2Id = ?"
            );

            PreparedStatement insertStmt = con.prepareStatement(
                "INSERT INTO matches (tournamentId, player1Id, player2Id, result) VALUES (?, ?, ?, ?)"
            );

            int successCount = 0;

            // Process each match result
            for (Map.Entry<String, String> entry : matchData.entrySet()) {
                String key = entry.getKey();
                String result = entry.getValue();

                // Parse the key to get player IDs (format: "player1Id-player2Id")
                String[] players = key.split("-");
                if (players.length != 2) {
                    continue; // Skip invalid entries
                }

                try {
                    int player1Id = Integer.parseInt(players[0]);
                    int player2Id = Integer.parseInt(players[1]);

                    // First delete any existing match
                    deleteStmt.setInt(1, tournamentId);
                    deleteStmt.setInt(2, player1Id);
                    deleteStmt.setInt(3, player2Id);
                    deleteStmt.executeUpdate();

                    // Convert N/A to N for database storage
                    if (result.equals("N/A")) {
                        result = "N";
                    }

                    // Then insert the new match
                    insertStmt.setInt(1, tournamentId);
                    insertStmt.setInt(2, player1Id);
                    insertStmt.setInt(3, player2Id);
                    insertStmt.setString(4, result);

                    successCount += insertStmt.executeUpdate();
                } catch (NumberFormatException e) {
                    // Skip entries with invalid player IDs
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

    @GetMapping("/tournaments/getMatches/{tournamentId}")
    public Map<String, String> getMatches(@PathVariable int tournamentId) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();
        Map<String, String> matchResults = new HashMap<>();

        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            // Prepare statement to get all matches for this tournament
            PreparedStatement stmt = con.prepareStatement(
                "SELECT player1Id, player2Id, result FROM matches WHERE tournamentId = ?"
            );
            stmt.setInt(1, tournamentId);
            ResultSet rs = stmt.executeQuery();

            // Process each match result
            while (rs.next()) {
                int player1Id = rs.getInt("player1Id");
                int player2Id = rs.getInt("player2Id");
                String result = rs.getString("result");

                // Convert N to N/A for frontend display
                if (result.equals("N")) {
                    result = "N/A";
                }

                // Store the match result with player IDs as the key
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
}
