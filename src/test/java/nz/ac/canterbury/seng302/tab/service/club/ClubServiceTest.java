package nz.ac.canterbury.seng302.tab.service.club;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@Sql(scripts = "/sql/club_service.sql")
class ClubServiceTest {
    
    private ClubService clubService;
    
    @Autowired
    private ClubRepository clubRepository;
    
    private TeamService teamService;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private UserEntity user;
    
    @BeforeEach
    void setup() {
        clubService = new ClubService();
        teamService = new TeamService(teamRepository);
        clubService.setTeamService(teamService);
        user = userRepository.findById(1L).get();
    }
    
    @Test
    void addTeamsToClub_twoTeams_successfullyAdded() {
    
        Club club = clubRepository.findById(388L);
        
        Assertions.assertEquals(0, club.getTeams().size());
        
        clubService.addTeamsToClub(club, List.of(1L, 2L), club.getAssociatedSport(), user);
    
        Assertions.assertEquals(2, club.getTeams().size());
    }
    
    @Test
    void addTeamsToClub_noTeams_noTeamsAdded() {
        
        Club club = clubRepository.findById(388L);
    
        Assertions.assertEquals(0, club.getTeams().size());
        
        clubService.addTeamsToClub(club, null , club.getAssociatedSport(), user);
        
        Assertions.assertEquals(0, club.getTeams().size());
    }
    
    @Test
    void addTeamsToClub_invalidTeams_noTeamsAdded() {
        
        Club club = clubRepository.findById(388L);
        
        Assertions.assertEquals(0, club.getTeams().size());
        
        clubService.addTeamsToClub(club, List.of(100L, 200L), club.getAssociatedSport(), user);
        
        Assertions.assertEquals(0, club.getTeams().size());
    }
    
}
