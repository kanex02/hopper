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
class ViewActivitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Unit test for testing if inputting an invalid id format results in a 404
     * @throws Exception if 404 is not thrown.
     */
    @Test
    @WithMockUser
    void formWithInvalidIdShouldThrow404() throws Exception {
        this.mockMvc.perform(
                        get("/activity/view/test")
                                .with(csrf())
                )
                .andExpect(status().is4xxClientError());
    }

    /**
     * Unit test for testing if inputting an incorrect id results in a correct redirection.
     * @throws Exception if expected redirection is not given.
     */
    @Test
    @WithMockUser
    void formWithDifferentIdShouldRedirect() throws Exception {
        this.mockMvc.perform(
                get("/activity/view/22")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection());
    }
}