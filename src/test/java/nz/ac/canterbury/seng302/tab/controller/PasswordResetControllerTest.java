package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Token;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PasswordResetControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private EmailService emailService;

    @BeforeAll
    void stubEmail() {
        doNothing().when(emailService).sendPasswordResetLinkEmail(any(UserEntity.class), any(Token.class));
    }

    @Test
    @WithMockUser
    void whenUserRequestsResetWithValidEmail_thenConfirmationMessageDisplayed() throws Exception {

        mockMvc.perform(post("/resetPassEmail")
                        .param("enterEmail", "jeff@gsnail.con")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkResetPasswordEmail"));
    }

    @Test
    @WithMockUser
    void whenUserRequestsResetWithRecognisedEmail_thenEmailIsSent() throws Exception {

        mockMvc.perform(post("/resetPassEmail")
                        .param("enterEmail", "test2@example.com")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendPasswordResetLinkEmail(any(UserEntity.class), any(Token.class));
    }

    @Test
    @WithMockUser
    void whenUserRequestsResetWithUnrecognisedEmail_thenEmailIsNotSent() throws Exception {

        mockMvc.perform(post("/resetPassEmail")
                        .param("enterEmail", "jeff@gsnail.con")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(emailService, times(0)).sendPasswordResetLinkEmail(any(UserEntity.class), any(Token.class));
    }

    @Test
    @WithMockUser
    void whenUserRequestsResetWithWithInvalidEmail_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/resetPassEmail")
                        .param("enterEmail", "F4b1an1$c00l")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

