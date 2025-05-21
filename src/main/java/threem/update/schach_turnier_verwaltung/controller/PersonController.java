package threem.update.schach_turnier_verwaltung.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.data.Person;

import java.io.IOException;
import java.sql.*;

@RestController
public class PersonController {

    private String url = "jdbc:derby:C:/Users/Samuel/Desktop/MGIN_PROJECTS/Schach_Turnier_Verwaltung/DB/database";
    private String user = "DBAdmin";
    private String dbpassword = "DBAdmin";

    @GetMapping("/persons/person/{username}/{password}")
    public String getPerson(@PathVariable String username, @PathVariable String password) {
        try {
            Connection con = DriverManager.getConnection(url,user,dbpassword);
            PreparedStatement pstmt = con.prepareStatement("select * from persons WHERE username = ? AND password =?");
            pstmt.setString(1,username);
            pstmt.setString(2,password);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            Person temp = new Person(rs.getInt("personId"),rs.getString("username"),rs.getString("password"),rs.getBoolean("admin"),rs.getInt("wins"),rs.getInt("losses"),rs.getInt("draws"));
            rs.close();
            con.close();

            // Convert Person object to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(temp);

            return jsonString;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
