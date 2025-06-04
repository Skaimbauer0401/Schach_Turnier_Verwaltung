package threem.update.schach_turnier_verwaltung.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncodeService {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public EncodeService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Default constructor for direct instantiation
    public EncodeService() {
        // Create a default BCryptPasswordEncoder when autowiring is not available
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String registerUser(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

    public boolean isPasswordMatch(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
