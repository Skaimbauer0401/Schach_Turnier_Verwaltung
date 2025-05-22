package threem.update.schach_turnier_verwaltung.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.data.Tournament;

import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
public class TournamentController {

    @GetMapping("/tournaments/newtournament/{tournament_name}/{start_time}/{end_time}")
    public String newTournament(@PathVariable String tournament_name, @PathVariable String start_time, @PathVariable String end_time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date parsedStartDate = dateFormat.parse(start_time);
        Date parsedEndDate = dateFormat.parse(end_time);

        Timestamp start = new Timestamp(parsedStartDate.getTime());
        Timestamp end = new Timestamp(parsedEndDate.getTime());

        Tournament tournament = new Tournament(1, "Tournament", start, end);



        return "Tournament erstellt";
    }
}
