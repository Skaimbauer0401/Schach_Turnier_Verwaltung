package threem.update.schach_turnier_verwaltung.data;

import java.util.ArrayList;

public class Person {
    private int id;
    private String username;
    private String password;
    private ArrayList<Tournament> tournaments;
    private boolean admin;
    private int wins;
    private int losses;
    private int draws;

    public Person() {
        this.tournaments = new ArrayList<>();
    }

    public Person(int id, String username, String password, boolean admin, int wins, int losses, int draws) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tournaments = new ArrayList<>();
        this.admin = admin;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(ArrayList<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", admin=" + admin +
                ", wins=" + wins +
                ", losses=" + losses +
                ", draws=" + draws +
                '}';
    }
}
