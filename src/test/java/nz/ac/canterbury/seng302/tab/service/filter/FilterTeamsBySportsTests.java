package nz.ac.canterbury.seng302.tab.service.filter;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @deprecated Please update to confirm to new <a href="https://eng-git.canterbury.ac.nz/seng302-2023/team-1000/-/wikis/Testing-Policy">testing policy</a>
 * (Really just the class name for this one)
 */
@Deprecated
@DataJpaTest
@Sql(scripts = "/sql/filter_teams.sql")
class FilterTeamsBySportsTests {
    @Autowired
    TeamRepository teamRepository;

    TeamService teamService;

    @BeforeEach
    void setup() {
        teamService = new TeamService(teamRepository);
    }

    @Test
    void filterTeams_selectedValidSport_filteredTeamsShown() {
        List<Team> teamList = teamService.filterSports(teamRepository.findAll(), "basketball");
        assertEquals(3, teamList.size());
        for (Team team : teamList) {
            assertEquals("basketball", team.getSport().getName());
        }
    }

    @Test
    void filterTeams_noSport_allTeamsShown() {
        List<Team> teamsList = teamService.filterSports(teamRepository.findAll(), null);
        assertEquals(5, teamsList.size());
    }

    @Test
    void filterTeams_selectedUnknownCity_noTeamsShown() {
        List<Team> teamsList = teamService.filterSports(teamRepository.findAll(), "Dog");
        assertEquals(0, teamsList.size());
    }

    @Test
    void filterTeams_emptyString_noTeamsShown() {
        List<Team> teamsList = teamService.filterSports(teamRepository.findAll(), "");
        assertEquals(0, teamsList.size());
    }

    @Test
    void filterTeams_multipleCities_filteredTeamsShown() {
        List<Team> teamsList = teamService.filterSports(teamRepository.findAll(), "football,basketball");
        assertEquals(5, teamsList.size());
        for (Team team : teamsList) {
            assertTrue(team.getSport().getName().equals("football") ||
                    team.getSport().getName().equals("basketball"));
        }
    }

    @Test
    void filterTeams_duplicateSports_noDuplicateTeams() {
        List<Team> teamsList = teamService.filterSports(teamRepository.findAll(), "basketball,basketball");
        assertEquals(3, teamsList.size());
        for (Team team : teamsList) {
            assertEquals("basketball", team.getSport().getName());
        }
    }

}
