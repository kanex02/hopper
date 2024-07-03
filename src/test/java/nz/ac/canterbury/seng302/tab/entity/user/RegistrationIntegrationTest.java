package nz.ac.canterbury.seng302.tab.entity.user;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import nz.ac.canterbury.seng302.tab.validation.UserFormInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import nz.ac.canterbury.seng302.tab.entity.Token;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;


    @Test
    void whenRegisterUserAndValidUser_thenEmailIsSent() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("password", "SecurePassword123!#%")
                        .param("confirmPassword", "SecurePassword123!#%")
                        .param("firstName", "If you see this email")
                        .param("lastName", "Then panic")
                        .param("email", "s302team1000@cosc.canterbury.ac.nz")
                        .param("dateOfBirth", "1969-04-20")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
        );

        verify(emailService, times(1)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

    }

    @Test
    void whenRegisterUserAndValidUser_thenDirectedToUserPage() throws Exception {

        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .with(csrf())
                                .param("password", "SecurePassword123!#%")
                                .param("confirmPassword", "SecurePassword123!#%")
                                .param("firstName", "If you see this email")
                                .param("lastName", "Then panic")
                                .param("email", "s302team1000@cosc.canterbury.ac.nz")
                                .param("dateOfBirth", "1969-04-20")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.redirectedUrl(null));
    }

    @Test
    void whenRegisterUserAndInvalidUser_thenNoEmailIsSent() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("password", "SecurePassword123!#%")
                        .param("confirmPassword", "SecurePassword123!#%")
                        .param("firstName", "123")
                        .param("lastName", "456")
                        .param("email", "notanemail@")
                        .param("dateOfBirth", "AAAA-03-07")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
        );

        verify(emailService, times(0)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    }

    @Test
    void whenRegisterUserAndWeakPassword_thenNoEmailIsSent() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("password", "password1")
                        .param("confirmPassword", "p4ssword1")
                        .param("firstName", "DOWNLOAD MUSESWIPR")
                        .param("lastName", "ON STEAM NOW")
                        .param("email", "fabian.gilson@canterbury")
                        .param("dateOfBirth", "1923-03-07")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
        );

        verify(emailService, times(0)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    }


    @Test
    void whenRegisterUserAndInvalidUser_thenErrorIsShown() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .with(csrf())
                                .param("password", "SecurePassword123!#%")
                                .param("confirmPassword", "SecurePassword123!#%")
                                .param("firstName", "123")
                                .param("lastName", "456")
                                .param("email", "notanemail@")
                                .param("dateOfBirth", "AAAA-03-07")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("userRegister"));
    }

    @Test
    void whenRegisterUserAndWeakPassword_thenErrorIsShown() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .with(csrf())
                                .param("password", "password1")
                                .param("confirmPassword", "p4ssword1")
                                .param("firstName", "DOWNLOAD MUSESWIPR")
                                .param("lastName", "ON STEAM NOW")
                                .param("email", "fabian.gilson@canterbury.com")
                                .param("dateOfBirth", "1923-03-07")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                )
                .andExpect(MockMvcResultMatchers.model().attributeExists("passwordPasswordError"));

        verify(emailService, times(0)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

    }

    @Test
    void whenRegisterDuplicateUser_thenFlashAttributeErrorAppears() throws Exception {

        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));

        var user = new UserFormInput(
                "Download-MuseSwipr",
                "On-Steam-Now",
                "museswiprIsCool@gmail.com",
                "1970-01-01",
                "SecurePassword123!#%",
                "SecurePassword123!#%"
        );

        // register first user
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .with(csrf())
                                .param("password", user.password())
                                .param("confirmPassword", user.confirmPassword())
                                .param("firstName", user.firstName())
                                .param("lastName", user.lastName())
                                .param("email", user.email())
                                .param("dateOfBirth", user.dateOfBirth())
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.redirectedUrl(null));

        // try to register another user with the same email
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/register")
                                .with(csrf())
                                .param("password", user.password())
                                .param("confirmPassword", user.confirmPassword())
                                .param("firstName", user.firstName())
                                .param("lastName", user.lastName())
                                .param("email", user.email()) // uses same email
                                .param("dateOfBirth", user.dateOfBirth())
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("userRegister"));

        verify(emailService, times(1)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    }
    
    @Test
    void registerNewUser_existingEmailIgnoreCase_errorShown() throws Exception {
        doNothing().when(emailService).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    
        var user = new UserFormInput(
            "Download-MuseSwipr",
            "On-Steam-Now",
            "museswipriscool@gmail.com",
            "1970-01-01",
            "SecurePassword123!#%",
            "SecurePassword123!#%"
        );
        
        // register first user
        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                    .with(csrf())
                    .param("password", user.password())
                    .param("confirmPassword", user.confirmPassword())
                    .param("firstName", user.firstName())
                    .param("lastName", user.lastName())
                    .param("email", user.email())
                    .param("dateOfBirth", user.dateOfBirth().toString())
                    .param("city", "Christchurch")
                    .param("country", "New Zealand")
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.redirectedUrl(null));
    
        // try to register another user with the same email with all upper case
        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                    .with(csrf())
                    .param("password", user.password())
                    .param("confirmPassword", user.confirmPassword())
                    .param("firstName", user.firstName())
                    .param("lastName", user.lastName())
                    .param("email", user.email().toUpperCase()) // uses same email
                    .param("dateOfBirth", user.dateOfBirth().toString())
                    .param("city", "Christchurch")
                    .param("country", "New Zealand")
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("userRegister"));
    
        verify(emailService, times(1)).sendConfirmationEmail(any(UserEntity.class), any(Token.class));
    }
}
