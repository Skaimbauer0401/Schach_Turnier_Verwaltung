package threem.update.schach_turnier_verwaltung.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import threem.update.schach_turnier_verwaltung.backend.data.Person;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

@Service
public class PersonService {
    public String getPerson(String username, String password){
        try {
            Connection con = databaseConnection();

            PreparedStatement pstmt = con.prepareStatement("select * from persons");
            ResultSet rsPerson = pstmt.executeQuery();
            Person person = null;

            boolean login = false;

            while(rsPerson.next() && !login){
                person = new Person(rsPerson.getInt("personId"), rsPerson.getString("username"), rsPerson.getString("password"), rsPerson.getBoolean("admin"), rsPerson.getInt("wins"), rsPerson.getInt("losses"), rsPerson.getInt("draws"));

                boolean passwordMatch = new EncodeService().isPasswordMatch(password, rsPerson.getString("password"));
                login = passwordMatch && username.equals(person.getUsername());

                if(login){
                    break;
                }
            }
            if(!login){
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


            String jsonPerson = toJson(person);

            String jsonTournaments = "";

            if (person.isAdmin()) {
                Statement stmt = con.createStatement();
                ResultSet rstournaments = stmt.executeQuery("SELECT * FROM tournaments");
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + toJson(tournament);
                }
            } else {
                PreparedStatement ptournstmt = con.prepareStatement("SELECT DISTINCT t.* FROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID WHERE pt.personID = ?");
                ptournstmt.setInt(1, person.getId());
                ResultSet rstournaments = ptournstmt.executeQuery();
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + toJson(tournament);
                }
            }

            rsPerson.close();
            con.close();

            return "[" + jsonPerson + jsonTournaments + "]";
        } catch (SQLException e) {
            return "SQL Fehler";
        } catch (Exception e) {
            return "Unbekannter Fehler";
        }
    }

    public String getPersonEnc(String username, String password){
        try {
            Connection con = databaseConnection();

            PreparedStatement pstmt = null;
            pstmt = con.prepareStatement("select * from persons WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rsPerson = pstmt.executeQuery();
            Person person = null;

            if(rsPerson.next()) {
                person = new Person(rsPerson.getInt("personId"), rsPerson.getString("username"), rsPerson.getString("password"), rsPerson.getBoolean("admin"), rsPerson.getInt("wins"), rsPerson.getInt("losses"), rsPerson.getInt("draws"));
            }else{
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


            String jsonPerson = toJson(person);

            String jsonTournaments = "";

            if (person.isAdmin()) {
                Statement stmt = con.createStatement();
                ResultSet rstournaments = stmt.executeQuery("SELECT * FROM tournaments");
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + toJson(tournament);
                }
            } else {
                PreparedStatement ptournstmt = con.prepareStatement("SELECT DISTINCT t.* FROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID WHERE pt.personID = ?");
                ptournstmt.setInt(1, person.getId());
                ResultSet rstournaments = ptournstmt.executeQuery();
                while (rstournaments.next()) {
                    Tournament tournament = new Tournament(rstournaments.getInt("tournamentId"), rstournaments.getString("name"), rstournaments.getTimestamp("start_time"), rstournaments.getTimestamp("end_time"));
                    jsonTournaments += "," + toJson(tournament);
                }
            }

            rsPerson.close();
            con.close();

            return "[" + jsonPerson + jsonTournaments + "]";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String newPerson(String username, String password, String adminkey){
        Person person;
        try {
            if (adminkey.equals("adminkey")) {
                person = new Person(username, password, true, 0, 0, 0);
            } else {
                person = new Person(username, password, false, 0, 0, 0);
            }

            Connection con = databaseConnection();

            PreparedStatement personstmt = con.prepareStatement("SELECT COUNT(*) FROM persons WHERE username = ?");
            personstmt.setString(1, person.getUsername());
            ResultSet rs = personstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) >= 1) {
                return "Benutzername bereits vergeben";
            }

            PreparedStatement pstmt = con.prepareStatement("INSERT INTO persons (username, password, admin, wins, losses, draws) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, person.getUsername());
            pstmt.setString(2, new EncodeService().registerUser(person.getPassword()));
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
