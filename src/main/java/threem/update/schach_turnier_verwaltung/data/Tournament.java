package threem.update.schach_turnier_verwaltung.data;

import java.util.ArrayList;
import java.util.Date;

public class Tournament {
    private int tournamentId;
    private String name;
    private Date start;
    private Date end;
    private ArrayList<Person> players;

    public Tournament(int tournamentId, String name, Date start, Date end) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
