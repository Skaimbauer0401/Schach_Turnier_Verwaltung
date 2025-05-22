package threem.update.schach_turnier_verwaltung.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.data.Person;
import threem.update.schach_turnier_verwaltung.data.Tournament;

import java.io.IOException;
import java.sql.*;

@RestController
public class PersonController {

    private String url = "jdbc:derby:C:/Users/Samuel/Desktop/MGIN_PROJECTS/Schach_Turnier_Verwaltung/DB/database";
    private String user = "DBAdmin";
    private String dbpassword = "DBAdmin";


    //gibt die zugehörige Person mit angemeldeten Tournaments zurück. beim admin alle existierenden tournaments
    @GetMapping("/persons/person/{username}/{password}")
    public String getPerson(@PathVariable String username, @PathVariable String password) {
        try {
            Connection con = DriverManager.getConnection(url,user,dbpassword);
            PreparedStatement pstmt = con.prepareStatement("select * from persons WHERE username = ? AND password =?");
            pstmt.setString(1,username);
            pstmt.setString(2,password);
            ResultSet rsPerson = pstmt.executeQuery();
            rsPerson.next();
            Person person = new Person(rsPerson.getInt("personId"),rsPerson.getString("username"),rsPerson.getString("password"),rsPerson.getBoolean("admin"),rsPerson.getInt("wins"),rsPerson.getInt("losses"),rsPerson.getInt("draws"));
            String jsonTournaments ="";

            if(person.isAdmin()){
                Statement stmt = con.createStatement();
                ResultSet rstournaments = stmt.executeQuery("SELECT * FROM tournaments");
                ObjectMapper objectMapper = new ObjectMapper();
                while(rstournaments.next()){
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"),rstournaments.getString("name"),rstournaments.getDate("start_time"),rstournaments.getDate("end_time"));
                    jsonTournaments += objectMapper.writeValueAsString(tournament);
                }
            }else{
                PreparedStatement ptournstmt = con.prepareStatement("SELECT t.* fROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID WHERE pt.personID = ?");
                ptournstmt.setInt(1,person.getId());
                ResultSet rstournaments = ptournstmt.executeQuery();
                ObjectMapper objectMapper = new ObjectMapper();
                while(rstournaments.next()){
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"),rstournaments.getString("name"),rstournaments.getDate("start_time"),rstournaments.getDate("end_time"));
                    jsonTournaments += objectMapper.writeValueAsString(tournament);
                }
            }

            rsPerson.close();
            con.close();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPerson = objectMapper.writeValueAsString(person);

            return jsonPerson+jsonTournaments;

        } catch (SQLException e) {
            return "SQL Fehler";
        } catch (JsonProcessingException e) {
            return "Json konvertierung fehlgeschlagen";
        }
    }
}
