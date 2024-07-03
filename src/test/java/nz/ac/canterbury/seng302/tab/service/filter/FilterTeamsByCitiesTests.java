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
class FilterTeamsByCitiesTests {
    @Autowired
    TeamRepository teamRepository;

    TeamService teamService;

    @BeforeEach
    void setup() {
        teamService = new TeamService(teamRepository);
    }

    @Test
    void filterTeams_selectedValidCity_filteredTeamsShown() {
        List<Team> teamList = teamService.filterCities(teamRepository.findAll(), "Dallas");
        assertEquals(2, teamList.size());
        for (Team team : teamList) {
            assertEquals("Dallas", team.getLocation().getCity());
        }
    }

    @Test
    void filterTeams_noCity_allTeamsShown() {
        List<Team> teamsList = teamService.filterCities(teamRepository.findAll(), null);
        assertEquals(5, teamsList.size());
    }

    @Test
    void filterTeams_selectedUnknownCity_noTeamsShown() {
        List<Team> teamsList = teamService.filterCities(teamRepository.findAll(), "Dog");
        assertEquals(0, teamsList.size());
    }

    @Test
    void filterTeams_emptyString_noTeamsShown() {
        List<Team> teamsList = teamService.filterCities(teamRepository.findAll(), "");
        assertEquals(0, teamsList.size());
    }

    @Test
    void filterTeams_multipleCities_filteredTeamsShown() {
        List<Team> teamsList = teamService.filterCities(teamRepository.findAll(), "Dallas,New York");
        assertEquals(4, teamsList.size());
        for (Team team : teamsList) {
            assertTrue(team.getLocation().getCity().equals("Dallas") ||
                    team.getLocation().getCity().equals("New York"));
        }
    }

    @Test
    void filterTeams_duplicateCities_noDuplicateTeams() {
        List<Team> teamsList = teamService.filterCities(teamRepository.findAll(), "Dallas,Dallas");
        assertEquals(2, teamsList.size());
        for (Team team : teamsList) {
            assertEquals("Dallas", team.getLocation().getCity());
        }
    }

}
