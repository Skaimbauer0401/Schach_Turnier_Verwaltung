package threem.update.schach_turnier_verwaltung.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity in this application
            .cors(cors -> cors.configure(http))  // Use CORS configuration from SchachTurnierVerwaltungApp
            .authorizeHttpRequests(auth -> auth
                // Person controller endpoints
                .requestMatchers("/persons/getperson/**").permitAll()  // Allow login endpoint
                .requestMatchers("/persons/newperson/**").permitAll()  // Allow registration endpoint
                .requestMatchers("/persons/allpersons").permitAll()  // Allow access to all persons
                .requestMatchers("/persons/addpersontotournament/**").permitAll()  // Allow tournament registration
                .requestMatchers("/persons/getPersonByTournament/**").permitAll()  // Allow tournament player listing

                // Tournament controller endpoints
                .requestMatchers("/tournaments/newtournament/**").permitAll()  // Allow tournament creation
                .requestMatchers("/tournaments/altertournament/**").permitAll()  // Allow tournament modification
                .requestMatchers("/tournaments/deletetournament/**").permitAll()  // Allow tournament deletion
                .requestMatchers("/matches/addmatches/**").permitAll()  // Allow adding match results
                .requestMatchers("/matches/getmatches//**").permitAll()  // Allow retrieving match results

                .anyRequest().permitAll()  // For now, allow all other requests too
            );

        return http.build();
    }
}
