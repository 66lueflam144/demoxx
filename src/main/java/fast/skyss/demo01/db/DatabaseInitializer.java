package fast.skyss.demo01.db;

import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    @Autowired
    public DatabaseInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (userRepository.findByUsername("dean") == null) {
            User admin = new User();
            admin.setUsername("dean");
            admin.setPassword("dean-on-line"); // Vulnerable: Plain text password
            admin.setEmail("dean@dropit.com");
            admin.setAdmin(true);
            userRepository.save(admin);
        }
    }
}
