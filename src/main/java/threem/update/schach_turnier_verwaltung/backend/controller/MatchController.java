package threem.update.schach_turnier_verwaltung.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import threem.update.schach_turnier_verwaltung.backend.services.MatchService;

import java.util.Map;

@Controller
public class MatchController {

    private MatchService matchService;

    @PostMapping("/tournaments/addMatches/{tournamentId}")
    public String addMatches(@PathVariable int tournamentId, @RequestBody Map<String, String> matchData) {
        matchService = new MatchService();
        return matchService.addMatches(tournamentId, matchData);
    }

    @GetMapping("/tournaments/getMatches/{tournamentId}")
    public Map<String, String> getMatches(@PathVariable int tournamentId) {
        matchService = new MatchService();
        return matchService.getMatches(tournamentId);

    }
}
