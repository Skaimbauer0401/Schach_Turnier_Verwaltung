package threem.update.schach_turnier_verwaltung.backend.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerUser(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

    public boolean isPasswordMatch(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
