package nz.ac.canterbury.seng302.tab.service.filter;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
@Sql(scripts = "/sql/filter_users_cities.sql")
class FilterUsersByCityTests {

    @Autowired
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }

    @Test
    void filterUsers_selectedValidCity_filteredUsersShown() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), "Christchurch");
        assertEquals(2, usersList.size());
        for (UserEntity user : usersList) {
            assertEquals("Christchurch", user.getLocation().getCity());
        }
    }

    @Test
    void filterUsers_noCity_allUsersShown() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), null);
        assertEquals(4, usersList.size());
    }

    @Test
    void filterUsers_selectedUnknownCity_noUsersShown() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), "Dog");
        assertEquals(0, usersList.size());
    }

    @Test
    void filterUsers_emptyString_noUsersShown() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), "");
        assertEquals(0, usersList.size());
    }

    @Test
    void filterUsers_multipleCities_filteredUsersShown() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), "Christchurch,London");
        assertEquals(4, usersList.size());
        for (UserEntity user : usersList) {
            assertTrue(user.getLocation().getCity().equals("Christchurch") ||
                    user.getLocation().getCity().equals("London"));
        }
    }

    @Test
    void filterUsers_duplicateCities_noDuplicateUsers() {
        List<UserEntity> usersList = userService.filterCities(userRepository.findAll(), "Christchurch,Christchurch");
        assertEquals(2, usersList.size());
        for (UserEntity user : usersList) {
            assertEquals("Christchurch", user.getLocation().getCity());
        }
    }
}
