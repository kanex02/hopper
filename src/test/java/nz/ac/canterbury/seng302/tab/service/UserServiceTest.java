package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserEntity user;

    Location location;

    String originalHash;

    UserService userService;

    EmailService emailService;

    @BeforeAll
    void setup() {
        Location location = new Location();
        location.setCountry("England");
        location.setCity("Gambia");
        String email = "email@gmail.com";
        user = new UserEntity("Bencm123!" ,"Jack", "Smith", email, "1111-11-11", new HashSet<>(), location);

        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.hashPassword(passwordEncoder);
        originalHash = user.getPassword();

        emailService = Mockito.mock(EmailService.class);
        Mockito.doNothing().when(emailService).sendUpdatePasswordNotificationEmail(Mockito.any());

        userService = new UserService(userRepository);
        userService.setEmailService(emailService);
        userService.setPasswordEncoder(passwordEncoder);

        userRepository.save(user);
    }

    @Test
    void updatePassword() {
        userService.updatePassword(user, "Bencm321!", "Bencm321!");
        UserEntity updatedUser = userRepository.findByEmailIgnoreCase(user.getEmail());

        Assertions.assertNotEquals(originalHash, updatedUser.getPassword());
    }

    @Test
    void differentPasswordsDoNotUpdate() {
        userService.updatePassword(user, "APass123!", "DiffPass123!");
        UserEntity updatedUser = userRepository.findByEmailIgnoreCase(user.getEmail());

        Assertions.assertEquals(originalHash, updatedUser.getPassword());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "XXXXXJack!12",
                    "XXXXXJACK!12",
                    "XXXXXSmith!12",
                    "XXXXXSMITH!12",
                    "PASSWORDjacksmith@gmail.com1234",
                    "p1D@",
                    "passwordisverylong",
                    "123",
                    "passworD",
                    "1234567890",
                    "Password1",
                    "",
                    " "
            }
    )
    void weakPasswordsAreNotUpdated(String password) {
        userService.updatePassword(user, "APass123!", "DiffPass123!");
        UserEntity updatedUser = userRepository.findByEmailIgnoreCase(user.getEmail());

        Assertions.assertEquals(originalHash, updatedUser.getPassword());
    }
}