package threem.update.schach_turnier_verwaltung.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;

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
}
