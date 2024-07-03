package nz.ac.canterbury.seng302.tab.service.search.team;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(scripts = "/sql/search_teams.sql")
class SearchTeamNameLocationTests {

    @Autowired
    private TeamRepository teamRepository;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(teamRepository);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "New York",
                    "new york",
                    "NEW YORK"
            }
    )
    void givenUniqueMatchingLocation_WhenSearch_TeamReturned(String teamInput) {
        List<Team> teams = teamService.search(teamInput);
        assertEquals(1, teams.size());
        //the line below is checking that the location of the team is the same as the input
        assertTrue(teams.stream().allMatch(team -> team.getLocation().getCity().equalsIgnoreCase(teamInput)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Heat",
                    "HEAT",
                    "heat"
            }
    )
    void givenUniqueRecognisedTeamName_WhenSearched_TeamReturned(String teamInput) {
        List<Team> teams = teamService.search(teamInput);
        assertEquals(1, teams.size());
        //the line below is checking that the name of the team is the same as the input
        assertTrue(teams.stream().allMatch(team -> team.getTeamName().equalsIgnoreCase(teamInput)));
    }


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Sounders",
                    "sounders",
                    "SOUNDERS"
            }
    )
    void givenNonUniqueName_WhenSearched_MultipleTeamsReturned(String teamInput) {
        List<Team> teams = teamService.search(teamInput);
        assertTrue(teams.size() > 1);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Seattle",
                    "seattle",
                    "SEATTLE"
            }
    )
    void givenCityWithMoreThanOneTeam_WhenSearched_MultipleTeamsReturned(String teamInput) {
        List<Team> teams = teamService.search(teamInput);
        assertTrue(teams.size() > 1);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "irl",
                    "girl",
                    "gir",
                    "wgirl"
            }
    )
    void givenSubstringOfRecognisedTeamName_WhenSearched_MatchingTeamReturned(String teamInput) {
        var teams = teamService.search(teamInput);
        assertEquals(1, teams.size());
        assertTrue(teams.stream().allMatch(team -> {
            String teamInputLowerCase = teamInput.toLowerCase();
            return team.getTeamName().toLowerCase().contains(teamInputLowerCase);
        }));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "New",
                    "ew y",
                    "Yor",
                    " yo",
                    "w y"
            }
    )
    void givenSubstringOfCityWithTeam_WhenSearched_TeamReturned(String teamInput) {
        var teams = teamService.search(teamInput);
        assertEquals(1, teams.size());
        assertTrue(teams.stream().allMatch(team -> {
            String teamInputLowerCase = teamInput.toLowerCase();
            return team.getLocation().getCity().toLowerCase().contains(teamInputLowerCase);
        }));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Miami Heat"
            }
    )
    void givenRecognisedCityAndTeamNamePair_WhenSearched_MatchingTeamReturned(String teamInput) {
        var teams = teamService.search(teamInput);
        assertEquals(1, teams.size());
        assertTrue(teams.stream().allMatch(team -> {
            String location = "Miami".toLowerCase();
            String teamName = "Heat".toLowerCase();
            return team.getLocation().getCity().toLowerCase().contains(location) ||
                    team.getTeamName().toLowerCase().contains(teamName);
        }));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Bears"
            }
    )
    void givenSearchStringWithMatches_WhenSearched_MatchesReturnedSortedByCapitalisation(String teamInput) {

        var teams = teamService.search(teamInput)
                .stream()
                .map((team) -> String.format("%s %s", team.getLocation().getCity(), team.getTeamName()))
                .toList();

        List<String> expected = List.of(
                "Chicago Bears",
                "Chicago bears"
        );

        assertEquals(expected, teams);
    }

    @Test
    void givenSearchStringWithMatches_WhenSearched_MatchesReturnedSortedAlphabetically() {

        var teams = teamService.search("m")
                .stream()
                .map((t) -> String.format("%s %s", t.getLocation().getCity(), t.getTeamName()))
                .toList();

        List<String> expected = List.of(
                "Houston Dynamo",
                "Mango Minecraft",
                "Miami Heat",
                "Minecraft Minecraft",
                "Toronto Maple Leafs"
        );

        assertEquals(expected, teams);
    }

    @Test
    void givenCityNameWithMatches_WhenSearched_MatchesReturnedSortedAlphabetically() {
        var teams = teamService.search("Dallas")
                .stream()
                .map((t) -> String.format("%s %s", t.getLocation().getCity(), t.getTeamName()))
                .toList();

        List<String> expected = List.of(
                "Dallas Cowboys",
                "Dallas Cowgirls"
        );

        assertEquals(expected, teams);
    }

    @Test
    void givenEmptySearchString_WhenSearched_NoTeamsReturned() {
        var result = teamService.search("");
        var expected = teamRepository.findAll();
        assertEquals(expected.size(), result.size());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "%%%",
                    "%Dallas_",
                    "!@#$%^&*()_+{}|:<>?",
                    "TheOuterWorlds",
                    "\\\\",
                    "____",
                    "////",
                    "\"\"\"",
                    "'''"
            }
    )
    void givenUnrecognisedSearchString_WhenSearched_NoTeamsReturned(String query) {
        List<Team> teams = teamService.search(query);
        assertTrue(teams.isEmpty());
    }
}
