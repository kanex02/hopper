package nz.ac.canterbury.seng302.tab.service.teamMembers;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@DataJpaTest
@Sql(scripts = "/sql/add_team_member.sql")
class AddMemberToTeamTests {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    private UserService userService;
    
    private TeamService teamService;
    
    @BeforeEach
    public void setup() {
        userService = new UserService(userRepository);
        teamService = new TeamService(teamRepository);
    }
    
    @Test
    void joinTeam_validUserAndTeamToken_userJoinsTeam() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("test");
        
        assertTrue(teamService.addUser(user, team, "member"));
        assertEquals(1, team.getMembers().size());
    }
    
    @Test
    void joinTeam_invalidTeamToken_doesNotJoinTeam() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("fake token");
    
        assertFalse(teamService.addUser(user, team, "member"));
        assertNull(team);
    }
    
    @Test
    void joinTeam_sameTeamTwice_onlyJoinOnce() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("test");
        
        teamService.addUser(user, team, "member");
    
        assertFalse(teamService.addUser(user, team, "member"));
        assertEquals(1, team.getMembers().size());
    }
    
    @Test
    void joinTeam_emptyTeamToken_doesNotJoinTeam() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("");
    
        assertFalse(teamService.addUser(user, team, "member"));
        assertNull(team);
    }
    
    @Test
    void joinTeam_invalidUser_doesNotJoinTeam() {
        UserEntity fakeUser = userService.getUserById(999L);
        Team team = teamService.getTeamByToken("test");
    
        assertFalse(teamService.addUser(fakeUser, team, "member"));
        assertNull(fakeUser);
    }
    
    @Test
    void joinTeam_userManagingTeam_doesNotJoinTeam() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("test");
        
        teamService.addUser(user, team, "manager");
        
        assertFalse(teamService.addUser(user, team, "member"));
    }
    
    @Test
    void joinTeam_userCoachingTeam_doesNotJoinTeam() {
        UserEntity user = userService.getUserById(1L);
        Team team = teamService.getTeamByToken("test");
        
        teamService.addUser(user, team, "coach");
        
        assertFalse(teamService.addUser(user, team, "member"));
    }
    
}
