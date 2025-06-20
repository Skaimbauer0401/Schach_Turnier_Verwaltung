package threem.update.schach_turnier_verwaltung.backend.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Tournament {
    private int tournamentId;
    private String name;
    private Timestamp start;
    private Timestamp end;
    private ArrayList<Person> players;

    public Tournament(int tournamentId, String name, Timestamp start, Timestamp end) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {this.tournamentId = tournamentId;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }
}
