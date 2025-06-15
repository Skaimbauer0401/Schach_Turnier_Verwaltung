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

    public EncodeService() {this.passwordEncoder = new BCryptPasswordEncoder();}

    public String registerUser(String rawPassword){
        return passwordEncoder.encode(rawPassword).replace("/", "_");
    }

    public boolean isPasswordMatch(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword.replace("_", "/"));
    }
}
