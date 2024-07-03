package nz.ac.canterbury.seng302.tab.runner;

import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runner for adding levels to users who do not have them.
 */
@Component
public class AddLevelsRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    public AddLevelsRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Adds levels to users who do not have them.
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        userRepository.findAll().forEach(user -> {
            if (user.getLevel() == null) {
                user.setLevel(1);
                userRepository.save(user);
            }
        });
    }
}
