package threem.update.schach_turnier_verwaltung.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    @GetMapping("/persons/person/{personId}")
    public String getPerson(@PathVariable int personId){

    }
}
