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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 */
@Deprecated
@DataJpaTest
@Sql(scripts = "/sql/add_team_member.sql")
class ChangeUserRolesTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    private UserService userService;
    private TeamService teamService;

    private UserEntity user;
    private Team team;

    private UserEntity user2;

    private Map<String, String> rolesMap;

    @BeforeEach
    public void setup() {
        userService = new UserService(userRepository);
        teamService = new TeamService(teamRepository);
        user = userService.getUserById(1L);
        team = teamService.getTeamByToken("test");
        user2 = userService.getUserById(2L);
        rolesMap = new HashMap<>();
    }

    @Test
    void addMember_changeRoleToCoach() {
        teamService.addUser(user, team, "member");
        teamService.addUser(user2, team, "manager");
        assertEquals("member", team.getUserRole(user));

        rolesMap.put(String.valueOf(user.getId()), "coach");

        teamService.updateRoles(rolesMap ,team.getId());

        assertEquals("coach", team.getUserRole(user));
        assertNotEquals("member", team.getUserRole(user));
    }

    @Test
    void addMember_changeRoleToManager() {
        teamService.addUser(user, team, "member");
        assertEquals("member", team.getUserRole(user));

        rolesMap.put(String.valueOf(user.getId()), "manager");

        teamService.updateRoles(rolesMap ,team.getId());


        assertEquals("manager", team.getUserRole(user));
        assertNotEquals("member", team.getUserRole(user));
    }

    @Test
    void addCoach_changeRoleToMember() {
        teamService.addUser(user, team, "coach");
        teamService.addUser(user2, team, "manager");
        assertEquals("coach", team.getUserRole(user));

        rolesMap.put(String.valueOf(user.getId()), "member");

        teamService.updateRoles(rolesMap ,team.getId());

        assertEquals("member", team.getUserRole(user));
        assertNotEquals("coach", team.getUserRole(user));
    }

    @Test
    void haveOneManager_tryToChangeManagerRole_throwsException() {
        teamService.addUser(user, team, "manager");
        assertEquals("manager", team.getUserRole(user));

        boolean noException = true;

        try {

            rolesMap.put(String.valueOf(user.getId()), "member");
            teamService.updateRoles(rolesMap ,team.getId());
        } catch (IllegalStateException e) {
            noException = false;
            assertEquals("manager", team.getUserRole(user));
        }

        if (noException) {
            fail();
        }
    }

    @Test
    void haveThreeManagers_addAnotherManager_throwsException() {
        user2 = userService.getUserById(2L);
        UserEntity user3 = userService.getUserById(3L);
        UserEntity user4 = userService.getUserById(4L);
        teamService.addUser(user, team, "manager");
        teamService.addUser(user2, team, "manager");
        teamService.addUser(user3, team, "manager");
        teamService.addUser(user4, team, "member");
        assertEquals("manager", team.getUserRole(user));
        assertEquals("manager", team.getUserRole(user2));
        assertEquals("manager", team.getUserRole(user3));

        try {
            rolesMap.put(String.valueOf(user4.getId()), "manager");
            teamService.updateRoles(rolesMap ,team.getId());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("manager", team.getUserRole(user));
            assertEquals("manager", team.getUserRole(user2));
            assertEquals("manager", team.getUserRole(user3));
        }
    }

    @Test
    void changeRoles_swapManagers_rolesSwappedCorrectly() {
        teamService.addUser(user, team, "manager");
        teamService.addUser(user2, team, "member");

        rolesMap.put(String.valueOf(user.getId()), "member");
        rolesMap.put(String.valueOf(user2.getId()), "manager");

        teamService.updateRoles(rolesMap ,team.getId());

        assertEquals("member", team.getUserRole(user));
        assertEquals("manager", team.getUserRole(user2));
    }

}
