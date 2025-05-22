package threem.update.schach_turnier_verwaltung.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.derby.shared.common.error.StandardException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.data.Person;
import threem.update.schach_turnier_verwaltung.data.Tournament;

import java.io.File;
import java.io.IOException;
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
            rsPerson.next();
            Person person = new Person(rsPerson.getInt("personId"), rsPerson.getString("username"), rsPerson.getString("password"), rsPerson.getBoolean("admin"), rsPerson.getInt("wins"), rsPerson.getInt("losses"), rsPerson.getInt("draws"));

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
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getDate("start_time"), rstournaments.getDate("end_time"));
                    jsonTournaments += objectMapper.writeValueAsString(tournament);
                }
            } else {
                PreparedStatement ptournstmt = con.prepareStatement("SELECT t.* fROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID WHERE pt.personID = ?");
                ptournstmt.setInt(1, person.getId());
                ResultSet rstournaments = ptournstmt.executeQuery();
                objectMapper = new ObjectMapper();
                //zugewiesene Tournaments in json String schreiben
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getDate("start_time"), rstournaments.getDate("end_time"));
                    jsonTournaments += objectMapper.writeValueAsString(tournament);
                }
            }

            rsPerson.close();
            con.close();

            return jsonPerson + jsonTournaments;
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
            if(rs.getInt(1)>=1){
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
        }catch (SQLException e) {
            return "SQL Fehler";
        }catch (Exception e){
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
        ResultSet rs = stmt.executeQuery("SELECT username FROM persons");

        ObjectMapper objectMapper;
        String jsonPerson = "";

        while(rs.next()){
            Person person = new Person(1,rs.getString("username") , "x", false, 0, 0, 0);

            objectMapper = new ObjectMapper();
            jsonPerson += objectMapper.writeValueAsString(person);
        }
        con.close();

        return jsonPerson;
    }
}
