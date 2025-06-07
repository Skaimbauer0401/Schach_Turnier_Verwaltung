package threem.update.schach_turnier_verwaltung.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.data.Tournament;
import threem.update.schach_turnier_verwaltung.backend.services.MatchService;
import threem.update.schach_turnier_verwaltung.backend.services.TournamentService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
