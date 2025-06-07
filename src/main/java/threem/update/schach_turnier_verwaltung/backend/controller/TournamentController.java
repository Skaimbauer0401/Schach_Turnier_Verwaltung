package threem.update.schach_turnier_verwaltung.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.services.MatchService;
import threem.update.schach_turnier_verwaltung.backend.services.TournamentService;

import java.text.ParseException;

@RestController
public class TournamentController {

    private TournamentService tournamentService;
    private MatchService matchService;

    @GetMapping("/tournaments/newtournament/{tournament_name}/{start_time}/{end_time}")
    public String newTournament(@PathVariable String tournament_name, @PathVariable String start_time, @PathVariable String end_time) throws ParseException {
        tournamentService = new TournamentService();
        return tournamentService.newTournament(tournament_name, start_time, end_time);
    }

    @GetMapping("/tournaments/altertournament/{tournamentId}/{name}/{start_time}/{end_time}")
    public String alterTournament(@PathVariable int tournamentId, @PathVariable String name, @PathVariable long start_time, @PathVariable long end_time) {
        tournamentService = new TournamentService();
        return tournamentService.alterTournament(tournamentId, name, start_time, end_time);
    }

    @GetMapping("/tournaments/deletetournament/{tournamentId}")
    public String deleteTournament(@PathVariable int tournamentId) {
        tournamentService = new TournamentService();
        return tournamentService.deleteTournament(tournamentId);
    }
}
