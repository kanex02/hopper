package nz.ac.canterbury.seng302.tab.service.team;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamFollowingTest {
    
    @Autowired
    private TeamRepository teamRepository;
    
    TeamService teamService = new TeamService(teamRepository);
    
    UserEntity user = new UserEntity();
    
    Team team = Team.createTestTeam();
    
    @Test
    void noTeam_followOrUnfollowTeam_nothingHappens() {
        teamService.followOrUnfollowTeam(null, user);
        Assertions.assertTrue(user.getFollowingTeams().isEmpty());
    }
    
    @Test
    void noUser_followOrUnfollowTeam_nothingHappens() {
        teamService.followOrUnfollowTeam(team, null);
        Assertions.assertTrue(team.getTeamFollowers().isEmpty());
    }
    
    @Test
    void userAlreadyFollowingTeam_unfollowTeam_followerRemoved() {
        team.getTeamFollowers().add(user);
        user.getFollowingTeams().add(team);
        
        teamService.followOrUnfollowTeam(team, user);
        Assertions.assertFalse(team.getTeamFollowers().contains(user));
        Assertions.assertFalse(user.getFollowingTeams().contains(team));
    }
    
    @Test
    void userNotFollowingTeam_followTeam_followerAdded() {
        teamService.followOrUnfollowTeam(team, user);
        Assertions.assertTrue(team.getTeamFollowers().contains(user));
        Assertions.assertTrue(user.getFollowingTeams().contains(team));
    }
    
    @Test
    void teamWithMultipleFollowers_followTeam_followerAdded() {
        UserEntity user2 = new UserEntity();
        
        team.getTeamFollowers().add(user);
        user.getFollowingTeams().add(team);
        
        teamService.followOrUnfollowTeam(team, user2);
        
        Assertions.assertTrue(team.getTeamFollowers().contains(user));
        Assertions.assertTrue(team.getTeamFollowers().contains(user2));
    }
    
    @Test
    void userWithMultipleFollowingTeams_followTeam_followerAdded() {
        Team team2 = Team.createTestTeam();
        
        user.getFollowingTeams().add(team);
        team.getTeamFollowers().add(user);
        
        teamService.followOrUnfollowTeam(team2, user);
        
        Assertions.assertTrue(user.getFollowingTeams().contains(team));
        Assertions.assertTrue(user.getFollowingTeams().contains(team2));
    }
}
