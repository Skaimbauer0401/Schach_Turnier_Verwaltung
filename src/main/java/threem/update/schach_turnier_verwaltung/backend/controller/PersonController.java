package threem.update.schach_turnier_verwaltung.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.data.Person;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;

import java.io.File;
import java.sql.*;

@RestController
public class PersonController {

    private String url;
    private String user = "DBAdmin";
    private String dbpassword = "DBAdmin";


    //gibt die zugehörige Person mit angemeldeten Tournaments zurück. beim admin alle existierenden tournaments
    @GetMapping("/persons/person/{username}/{password}")
    public String getPerson(@PathVariable String username, @PathVariable String password) {
        try {
            //Datenbank File finden
            File file = new File("DB/database");
            url = "jdbc:derby:" + file.getAbsolutePath();
            //Datenbank verbinden und Person mit login credentials auslesen
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            PreparedStatement pstmt = con.prepareStatement("select * from persons WHERE username = ? AND password =?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rsPerson = pstmt.executeQuery();
            Person person;
            if (rsPerson.next()) {
                person = new Person(rsPerson.getInt("personId"), rsPerson.getString("username"), rsPerson.getString("password"), rsPerson.getBoolean("admin"), rsPerson.getInt("wins"), rsPerson.getInt("losses"), rsPerson.getInt("draws"));
            } else {
                return "Benutzername oder Passwort falsch";
            }

            PreparedStatement pstmtwins = con.prepareStatement("SELECT COUNT(matchid) FROM matches WHERE PLAYER1ID = ? AND RESULT = ?");
            pstmtwins.setInt(1, person.getId());
            pstmtwins.setString(2, "W");

            PreparedStatement pstmtlosses = con.prepareStatement("SELECT COUNT(matchid) FROM matches WHERE PLAYER1ID = ? AND RESULT = ?");
            pstmtlosses.setInt(1, person.getId());
            pstmtlosses.setString(2, "L");

            PreparedStatement pstmtdraws = con.prepareStatement("SELECT COUNT(matchid) FROM matches WHERE PLAYER1ID = ? AND RESULT = ?");
            pstmtdraws.setInt(1, person.getId());
            pstmtdraws.setString(2, "D");

            ResultSet rswins = pstmtwins.executeQuery();
            ResultSet rslosses = pstmtlosses.executeQuery();
            ResultSet rsdraws = pstmtdraws.executeQuery();

            rswins.next();
            rslosses.next();
            rsdraws.next();

            person.setWins(rswins.getInt(1));
            person.setLosses(rslosses.getInt(1));
            person.setDraws(rsdraws.getInt(1));

            PreparedStatement pstmtupdate = con.prepareStatement("UPDATE persons SET wins = ?, losses = ?, draws = ? WHERE personId = ?");
            pstmtupdate.setInt(1, person.getWins());
            pstmtupdate.setInt(2, person.getLosses());
            pstmtupdate.setInt(3, person.getDraws());
            pstmtupdate.setInt(4, person.getId());
            pstmtupdate.executeUpdate();


            //Person zu json String
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPerson = objectMapper.writeValueAsString(person);

            String jsonTournaments = "";

            //wenn die Person Admin ist, werden alle Tournaments geladen, wenn nicht, dann werden die für den Spieler eingetragenen Tournaments geladen
            if (person.isAdmin()) {
                Statement stmt = con.createStatement();
                ResultSet rstournaments = stmt.executeQuery("SELECT * FROM tournaments");
                objectMapper = new ObjectMapper();
                //Alle Tournaments in json String schreiben
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + objectMapper.writeValueAsString(tournament);
                }
            } else {
                PreparedStatement ptournstmt = con.prepareStatement("SELECT DISTINCT t.* FROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID WHERE pt.personID = ?");
                ptournstmt.setInt(1, person.getId());
                ResultSet rstournaments = ptournstmt.executeQuery();
                objectMapper = new ObjectMapper();
                //zugewiesene Tournaments in json String schreiben
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + objectMapper.writeValueAsString(tournament);
                }
            }

            rsPerson.close();
            con.close();


            return "[" + jsonPerson + jsonTournaments + "]";
        } catch (SQLException e) {
            return "SQL Fehler";
        } catch (JsonProcessingException e) {
            return "Json konvertierung fehlgeschlagen";
        } catch (Exception e) {
            return "Unbekannter Fehler";
        }
    }

    @GetMapping("/persons/newperson/{username}/{password}/{adminkey}")
    public String newPerson(@PathVariable String username, @PathVariable String password, @PathVariable String adminkey) {
        Person person;
        try {
            if (adminkey.equals("adminkey")) {
                person = new Person(username, password, true, 0, 0, 0);
            } else {
                person = new Person(username, password, false, 0, 0, 0);
            }

            //Datenbank File finden
            File file = new File("DB/database");
            url = "jdbc:derby:" + file.getAbsolutePath();

            //Datenbank verbinden und Person hinzufügen, falls nicht bereits vorhanden
            Connection con = DriverManager.getConnection(url, user, dbpassword);
            PreparedStatement personstmt = con.prepareStatement("SELECT COUNT(personId) FROM persons WHERE username = ?");
            personstmt.setString(1, person.getUsername());
            ResultSet rs = personstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) >= 1) {
                return "Benutzername bereits vergeben";
            }

            //erstellen der neuen Person
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO persons (username, password, admin, wins, losses, draws) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, person.getUsername());
            pstmt.setString(2, person.getPassword());
            pstmt.setBoolean(3, person.isAdmin());
            pstmt.setInt(4, person.getWins());
            pstmt.setInt(5, person.getLosses());
            pstmt.setInt(6, person.getDraws());
            int result = pstmt.executeUpdate();
            con.close();

            return String.valueOf(result);
        } catch (SQLException e) {
            return "SQL Fehler";
        } catch (Exception e) {
            return "Unbekannter Fehler";
        }
    }

    //gibt von allen Personen nur den username an den aufrufer
    @GetMapping("/persons/allpersons")
    public String getAllPersons() throws SQLException, JsonProcessingException {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();

        //Datenbank verbinden und Person hinzufügen, falls nicht bereits vorhanden
        Connection con = DriverManager.getConnection(url, user, dbpassword);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT personid, username FROM persons ORDER BY username ASC");

        ObjectMapper objectMapper;
        String jsonPerson = "";

        while (rs.next()) {
            Person person = new Person(rs.getInt("personid"), rs.getString("username"), "nene das tust du nicht", false, 0, 0, 0);

            objectMapper = new ObjectMapper();
            jsonPerson += objectMapper.writeValueAsString(person);
        }
        con.close();

        return jsonPerson;
    }

    @GetMapping("/persons/person/addpersontotournament/{personId}/{tournamentId}")
    public String addPersontoTournament(@PathVariable int personId, @PathVariable int tournamentId) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();

        //Datenbank verbinden
        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            // First check if the tournament exists
            PreparedStatement checkTournament = con.prepareStatement("SELECT COUNT(*) FROM tournaments WHERE tournamentId = ?");
            checkTournament.setInt(1, tournamentId);
            ResultSet rs = checkTournament.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                con.close();
                return "Tournament existiert nicht";
            }

            // Check if the person is already registered for this tournament
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


    @GetMapping("/persons/person/getPersonByTournament/{tournamentId}")
    public ResponseEntity<?> getPersonsByTournament(@PathVariable int tournamentId) {
        File file = new File("DB/database");
        url = "jdbc:derby:" + file.getAbsolutePath();

        //Datenbank verbinden
        try {
            Connection con = DriverManager.getConnection(url, user, dbpassword);

            // First check if the tournament exists
            PreparedStatement result = con.prepareStatement("SELECT * FROM persons_tournaments pt JOIN persons p on pt.personId = p.personId WHERE pt.tournamentId = ?");
            result.setInt(1, tournamentId);
            ResultSet rs = result.executeQuery();
            String jsonPersons = "";
            while(rs.next()){
                Person person = new Person(rs.getInt("personId"), rs.getString("username"), "nene das tust du nicht", false, 0, 0, 0);
                ObjectMapper objectMapper = new ObjectMapper();
                jsonPersons += objectMapper.writeValueAsString(person)+",";
            }

            con.close();

            // Check if jsonPersons is empty (no players found)
            if (jsonPersons.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return new ResponseEntity<>("[]", headers, HttpStatus.OK);
            }

            // Remove the trailing comma
            jsonPersons = jsonPersons.substring(0, jsonPersons.length() - 1);

            // Set content type to application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("["+jsonPersons+"]", headers, HttpStatus.OK);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL Fehler");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JSON Fehler");
        }
    }
}
