package threem.update.schach_turnier_verwaltung.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import threem.update.schach_turnier_verwaltung.backend.data.Person;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TournamentService {

    public String newTournament(String tournament_name, String start_time, String end_time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.util.Date parsedStartDate = dateFormat.parse(start_time);
        Date parsedEndDate = dateFormat.parse(end_time);

        Timestamp start = new Timestamp(parsedStartDate.getTime());
        Timestamp end = new Timestamp(parsedEndDate.getTime());

        Tournament tournament = new Tournament(1, "Tournament", start, end);

        try {
            Connection con = databaseConnection();
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

    public String deleteTournament(int tournamentId) {
        try {
            Connection con = databaseConnection();

            PreparedStatement pstmtRefs = con.prepareStatement("DELETE FROM persons_tournaments WHERE tournamentId = ?");
            pstmtRefs.setInt(1, tournamentId);
            pstmtRefs.executeUpdate();

            PreparedStatement pstmt = con.prepareStatement("DELETE FROM tournaments WHERE tournamentId = ?");
            pstmt.setInt(1, tournamentId);
            pstmt.executeUpdate();
            con.close();

            PreparedStatement ptsmtmatches = con.prepareStatement("DELETE FROM matches WHERE tournamentId = ?");
            ptsmtmatches.setInt(1, tournamentId);
            ptsmtmatches.executeUpdate();

            return "Successfully deleted tournament " + tournamentId;
        } catch (SQLException e) {
            return "SQL Fehler: " + e.getMessage();
        } catch (Exception e) {
            return "Unbekannter Fehler: " + e.getMessage();
        }
    }

    public String alterTournament( int tournamentId,  String name,  long start_time,  long end_time){
        try {
            Connection con = databaseConnection();
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

    public String addPersonToTournament(int personId, int tournamentId){
        try {
            Connection con = databaseConnection();

            PreparedStatement checkTournament = con.prepareStatement("SELECT COUNT(*) FROM tournaments WHERE tournamentId = ?");
            checkTournament.setInt(1, tournamentId);
            ResultSet rs = checkTournament.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                con.close();
                return "Tournament existiert nicht";
            }

            PreparedStatement checkDuplicate = con.prepareStatement("SELECT COUNT(*) FROM persons_tournaments WHERE personId = ? AND tournamentId = ?");
            checkDuplicate.setInt(1, personId);
            checkDuplicate.setInt(2, tournamentId);
            ResultSet duplicateRs = checkDuplicate.executeQuery();
            duplicateRs.next();
            int duplicateCount = duplicateRs.getInt(1);

            if (duplicateCount > 0) {
                con.close();
                return "Spieler ist bereits für dieses Tournament registriert";
            }

            PreparedStatement pstmt = con.prepareStatement("INSERT INTO persons_tournaments (personID, tournamentID) VALUES (?, ?)");
            pstmt.setInt(1, personId);
            pstmt.setInt(2, tournamentId);
            int i = pstmt.executeUpdate();
            con.close();

            return String.valueOf(i);

        } catch (SQLException e) {
            return "SQL Fehler";
        }
    }

    public ResponseEntity<String> getPersonsByTournament(int tournamentId){
        try {
            Connection con = databaseConnection();

            PreparedStatement result = con.prepareStatement("SELECT personId, username FROM persons_tournaments pt JOIN persons p on pt.personId = p.personId WHERE pt.tournamentId = ?");
            result.setInt(1, tournamentId);
            ResultSet rs = result.executeQuery();
            String jsonPersons = "";
            while(rs.next()){
                Person person = new Person(rs.getInt("personId"), rs.getString("username"), "nene das tust du nicht", false, 0, 0, 0);
                jsonPersons += toJson(person) + ",";
            }

            con.close();

            if (jsonPersons.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>("[]", headers, HttpStatus.OK);
            }

            jsonPersons = jsonPersons.substring(0, jsonPersons.length() - 1);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("["+jsonPersons+"]", headers, HttpStatus.OK);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL Fehler");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JSON Fehler");
        }
    }




    public String toJson(Object object) throws JsonProcessingException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        }catch(Exception e){
            return "Json konvertierung fehlgeschlagen";
        }
    }

    public Connection databaseConnection(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/threem/update/schach_turnier_verwaltung/backend/database_important/database_connection"));
            String line = br.readLine();
            String url = line.split(";")[1];
            line = br.readLine();
            String username = line.split(";")[1];
            line = br.readLine();
            String dbpassword = line.split(";")[1];
            br.close();

            return DriverManager.getConnection(url,username,dbpassword);
        } catch (SQLException | IOException e) {
            System.out.println("DB Connection failed");
            return null;
        }
    }
}
