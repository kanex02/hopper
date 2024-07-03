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
@Sql(scripts = "/sql/filter_users_sports.sql")
class FilterUsersBySportsTests {

    @Autowired
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }

    @Test
    void filterUsers_selectedValidSports_filteredUsersShown() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), "football");
        assertEquals(3, usersList.size());
        for (UserEntity user : usersList) {
            assertTrue(user.getFavouriteSports().stream().anyMatch(sport -> sport.getName().equals("football")));
        }
    }

    @Test
    void filterUsers_noSports_allUsersShown() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), null);
        assertEquals(6, usersList.size());
    }

    @Test
    void filterUsers_selectedNewSport_noUsersShown() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), "golf");
        assertEquals(0, usersList.size());
    }

    @Test
    void filterUsers_emptyString_noUsersShown() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), "");
        assertEquals(0, usersList.size());
    }

    @Test
    void filterUsers_multipleSports_filteredUsersShown() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), "football,basketball");
        assertEquals(5, usersList.size());
        for (UserEntity user : usersList) {
            assertTrue(user.getFavouriteSports().stream().anyMatch(sport -> sport.getName().equals("football")
                    || sport.getName().equals("basketball")));
        }
    }

    @Test
    void filterUsers_duplicateSports_noDuplicateUsers() {
        List<UserEntity> usersList = userService.filterSports(userRepository.findAll(), "football,football");
        assertEquals(3, usersList.size());
        for (UserEntity user : usersList) {
            assertTrue(user.getFavouriteSports().stream().anyMatch(sport -> sport.getName().equals("football")));
        }
    }
}
