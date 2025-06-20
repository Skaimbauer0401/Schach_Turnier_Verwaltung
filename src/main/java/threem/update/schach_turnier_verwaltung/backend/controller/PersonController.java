package threem.update.schach_turnier_verwaltung.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import threem.update.schach_turnier_verwaltung.backend.services.PersonService;
import threem.update.schach_turnier_verwaltung.backend.services.TournamentService;

@RestController
public class PersonController {

    private PersonService personService;
    private TournamentService tournamentService;

    @GetMapping("/persons/getperson/{username}/{password}")
    public String getPerson(@PathVariable String username, @PathVariable String password) {
        personService = new PersonService();
        return personService.getPerson(username, password);
    }

    @GetMapping("/persons/getpersonenc/{username}/{password}")
    public String getPersonEnc(@PathVariable String username, @PathVariable String password){
        personService = new PersonService();
        return personService.getPersonEnc(username, password);
    }

    @GetMapping("/persons/newperson/{username}/{password}/{adminkey}")
    public String newPerson(@PathVariable String username, @PathVariable String password, @PathVariable String adminkey) {
        PersonService personService = new PersonService();
        return personService.newPerson(username, password, adminkey);
    }

    @GetMapping("/persons/addpersontotournament/{personId}/{tournamentId}")
    public String addPersontoTournament(@PathVariable int personId, @PathVariable int tournamentId) {
        tournamentService = new TournamentService();
        return tournamentService.addPersonToTournament(personId, tournamentId);
    }

    @GetMapping("/persons/getPersonByTournament/{tournamentId}")
    public ResponseEntity<?> getPersonsByTournament(@PathVariable int tournamentId) {
        tournamentService = new TournamentService();
        return tournamentService.getPersonsByTournament(tournamentId);
    }
}
