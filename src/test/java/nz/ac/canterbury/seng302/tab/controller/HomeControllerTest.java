package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;

    // TODO: Fix this test (Login does not disappear, th:if is used to hide the button)
//    @Test
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    public void loginButton_mockUser_loginButtonNotDisplayed() throws Exception {
//        Set<Sport> sports = new HashSet<>();
//        Location location = new Location("a1", "a2", "s", "c", "p", "c1");
//        UserEntity mockUser = new UserEntity("ss", "ss", "ss", "testuser",
//            "1111-11-11", sports, location);
//        when(userService.getUserByEmail("testuser")).thenReturn(mockUser);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andReturn();
//
//        String responseContent = result.getResponse().getContentAsString();
//        assertThat(responseContent).doesNotContain("Login");
//    }
}
