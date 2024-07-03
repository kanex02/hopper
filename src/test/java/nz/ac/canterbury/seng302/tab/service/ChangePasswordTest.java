package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 * (Really just the class name for this one)
 */
@Deprecated
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ChangePasswordTest {

    @Autowired
    private UserRepository userRepository;
    
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private UserEntity user;
    
    String originalHash;
    
    @BeforeAll
    void initialize() {
        Location location = new Location();
        location.setCountry("England");
        location.setCity("Gambia");
        String email = "email@gmail.com";
        user = new UserEntity("oldPassword" ,"Jack", "Smith", email, "1111-11-11", new HashSet<>(), location);
    
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.hashPassword(passwordEncoder);
        originalHash = user.getPassword();
    
        userService = new UserService(userRepository);
        userService.setPasswordEncoder(passwordEncoder);
        user.setPassword(passwordEncoder.encode("oldPassword"));
        user = userRepository.save(user);
    
        userRepository.save(user);
    }

    @Test
    void checkEnteredPassword_shouldReturnTrue_whenOldPasswordMatches() {
        Long userId = user.getId();
        boolean passwordMatch = userService.checkPasswordMatches(userId, "oldPassword");
        assertTrue(passwordMatch);
    }

    @Test
    void checkOldPassword_shouldReturnFalse_whenOldPasswordDoesNotMatch() {
        Long userId = user.getId();
        boolean passwordMatch = userService.checkPasswordMatches(userId, "incorrectPassword");
        assertFalse(passwordMatch);
    }
    
}