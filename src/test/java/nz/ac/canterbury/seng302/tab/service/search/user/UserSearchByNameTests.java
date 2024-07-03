package nz.ac.canterbury.seng302.tab.service.search.user;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
@Sql(scripts = "/sql/search_users.sql")
class UserSearchByNameTests {

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Gabbro Hornfels",
                    "Solanum Santander",
                    "Eskil Steffensen"
            }
    )
    void givenUniqueRecognisedFullName_WhenSearch_UserReturned(String name) {
        List<UserEntity> users = userService.search(name);
        assertEquals(1, users.size());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Gabbro",
                    "Solanum",
                    "Eskil",
                    "GABBRO",
                    "solanum",
                    "eSKiL"
            }
    )
    void givenUniqueRecognisedFirstName_WhenSearched_UserReturned(String firstName) {
        List<UserEntity> users = userService.search(firstName);
        assertEquals(1, users.size());
        assertTrue(users.stream().allMatch(user -> user.getFirstName().equalsIgnoreCase(firstName)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Hornfels",
                    "Santander",
                    "Steffensen",
                    "HORNFELS",
                    "santander",
                    "sTefFENseN"
            }
    )
    void givenUniqueRecognisedLastName_WhenSearched_UserReturned(String lastName) {
        List<UserEntity> users = userService.search(lastName);
        assertEquals(1, users.size());
        assertTrue(users.stream().allMatch(user -> user.getLastName().equalsIgnoreCase(lastName)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Twin",
                    "Onkel",
                    "Feldspar"
            }
    )
    void givenNonUniqueName_WhenSearched_MultipleUsersReturned(String name) {
        List<UserEntity> users = userService.search(name);
        assertTrue(users.size() > 1);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Feldspar",
                    "fEldSPAr",
                    "feldspar",
                    "FELDSPAR"
            }
    )
    void givenRecognisedName_WhenSearched_FirstNameAndLastNameMatchesReturned(String name) {
        List<UserEntity> users = userService.search(name);
        assertEquals(3, users.size());
        assertTrue(users.stream().allMatch(user -> {
            String lowerName = name.toLowerCase();
            return user.getFirstName().toLowerCase().equals(lowerName)
                    || user.getLastName().toLowerCase().equals(lowerName);
        }));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Outer",
                    "outer",
                    "OUTER",
                    "oUTer",
                    "Wilds",
                    "wILds",
                    "Wilds",
                    "entuREs",
                    "wilds ven"
            }
    )
    void givenSubstringOfRecognisedLastName_WhenSearched_MatchesReturned(String name) {
        List<UserEntity> users = userService.search(name);
        assertEquals(1, users.size());
        assertTrue(users.stream().allMatch(user -> {
            String nameLowerCase = name.toLowerCase();
            return user.getLastName().toLowerCase().contains(nameLowerCase);
        }));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "Dar",
                    "ark",
                    "bro",
                    "abbr",
                    "BRO",
                    "bRo"
            }
    )
    void givenSubstringOfRecognisedFirstName_WhenSearched_MatchesReturned(String name) {
        List<UserEntity> users = userService.search(name);
        assertEquals(1, users.size());
        assertTrue(users.stream().allMatch(user -> {
            String nameLowerCase = name.toLowerCase();
            return user.getFirstName().toLowerCase().contains(nameLowerCase);
        }));
    }

    @Test
    void givenSearchStringWithMatches_WhenSearched_MatchesReturnedSortedAlphabetically() {

        List<String> users = userService.search("h")
                .stream()
                .map((user) -> String.format("%s %s", user.getFirstName(), user.getLastName()))
                .toList();

        List<String> expected = List.of(
                "Gabbro Hornfels",
                "Gossan Chert",
                "Hal Brittleson",
                "Hal Marlson"
        );

        assertEquals(expected, users);
    }

    @Test
    void givenEmptySearchString_WhenSearched_NoUsersReturned() {
        List<UserEntity> users = userService.search("");
        List<UserEntity> allUsers = userRepository.findAll();
        assertEquals(allUsers.size(), users.size());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "%%%",
                    "%Gabbro_",
                    "!@#$%^&*()_+{}|:<>?",
                    "TheOuterWorlds",
                    "\\\\",
                    "____",
                    "////",
                    "\"\"\"",
                    "'''"
            }
    )
    void givenUnrecognisedSearchString_WhenSearched_NoUsersReturned(String query) {
        List<UserEntity> users = userService.search(query);
        assertTrue(users.isEmpty());
    }

}
