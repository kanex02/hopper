package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
class TeamsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void formWithInvalidIdShouldThrow404() throws Exception {
        this.mockMvc.perform(
                        get("/teams/test")
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void formWithNonExistentIdShouldThrow404() throws Exception {
        this.mockMvc.perform(
                        get("/teams/-1")
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }
}