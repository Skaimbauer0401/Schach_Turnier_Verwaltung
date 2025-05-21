package threem.update.schach_turnier_verwaltung.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class PersonController {

    private String url = "jdbc:derby:C:/Users/Samuel/Desktop/MGIN_PROJECTS/Schach_Turnier_Verwaltung/DB/database";
    private String user = "DBAdmin";
    private String dbpassword = "DBAdmin";

    @GetMapping("/persons/person/{username}/{password}")
    public String getPerson(@PathVariable String username, @PathVariable String password){
        try {
            Connection con = DriverManager.getConnection(url,user,dbpassword);
            PreparedStatement pstmt = con.prepareStatement("select * from person where username=? and password=?");
            pstmt.setString(1,username);
            pstmt.setString(2,password);
            ResultSet rs = pstmt.executeQuery();
            con.close();
            System.out.println(rs.getInt(1)+rs.getString(2)+rs.getString(3)+rs.getBoolean(4)+rs.getInt(5)+rs.getInt(6)+rs.getInt(7));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "tesmp";
    }
}
