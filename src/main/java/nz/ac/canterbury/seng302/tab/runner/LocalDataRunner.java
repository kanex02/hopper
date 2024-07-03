package nz.ac.canterbury.seng302.tab.runner;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogVisibility;
import nz.ac.canterbury.seng302.tab.entity.challenge.Challenge;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.repository.BlogPostRepository;
import nz.ac.canterbury.seng302.tab.repository.NotificationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.LineupRepository;
import nz.ac.canterbury.seng302.tab.repository.challenge.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Runner for creating some basic data for {@link Challenge}s for testing
 */
@Component
public class LocalDataRunner implements ApplicationRunner {

    private static final String LOCAL_PASS = "F4b1an1$c00l";
    private static final String FIRST_NAME = "Fabian";
    private static final String LAST_NAME = "Hornfels";
    private static final String START_DATE_TIME = "1970-05-15T11:43:09";
    private static final String START_DATE = "1970-05-15";

    /**
     * Repository access
     */
    @Autowired
    private ChallengeRepository challengeRepository;


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private LineupRepository lineupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.is_local}")
    private boolean isLocal;


    /**
     * Runner callback that creates a list of challenges and saves them in the database. Challenge data was generated
     * by ChatGPT.
     *
     * @param args incoming application arguments
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (isLocal) {
            Supplier<Location> location = () -> new Location("256 Waimairi Road", "", "Avonhead", "Christchurch", "8041", "NZ");

            Supplier<Location> emptyLocation = () -> new Location(null, null);

            UserEntity user0 = new UserEntity(LOCAL_PASS,
                    FIRST_NAME, LAST_NAME,
                    "test0@example.com", START_DATE, Set.of(), location.get());

            UserEntity user1 = new UserEntity(
                    LOCAL_PASS,
                    FIRST_NAME, LAST_NAME, "test1@example.com", START_DATE, Set.of(), null);

            UserEntity user2 = new UserEntity(LOCAL_PASS,
                    FIRST_NAME, LAST_NAME,
                    "test2@example.com", START_DATE, Set.of(), location.get());

            user2.follow(user0);
            user0.follow(user2);

            UserEntity user3 = new UserEntity(LOCAL_PASS,
                    FIRST_NAME, LAST_NAME, "test3@example.com", START_DATE, Set.of(), location.get());

            user0.hashPassword(passwordEncoder);
            user1.hashPassword(passwordEncoder);
            user2.hashPassword(passwordEncoder);
            user3.hashPassword(passwordEncoder);


            user2 = userRepository.save(user2);
            user0 = userRepository.save(user0);
            userRepository.saveAll(List.of(user1, user3));

            user0.confirmEmail();
            user1.confirmEmail();
            user2.confirmEmail();
            user3.confirmEmail();

            user2.setLevel(19);
            user2 = userRepository.save(user2);
            user0 = userRepository.save(user0);
            userRepository.saveAll(List.of(user1, user3));

            Sport sport = new Sport("Chess Boxing");

            Team team = new Team("SENG302TestTeam");
            team.setDateCreated(START_DATE_TIME);
            team.setSport(sport);
            team.setLocation(location.get());
            team.setJoinToken("ee442694e93c");
            team.addMember(user0);
            team.addMember(user1);
            team.addManager(user2);
            team = teamRepository.save(team);

            Activity activity = new Activity("Blah", "2024-05-15T11:43:09", "2024-06-15T11:43:09", ActivityType.GAME, team);
            activity.setUser(user2);
            activity.setLocation(emptyLocation.get());
            activity = activityRepository.save(activity);

            Supplier<Sport> sportSupplier = () -> new Sport("Raptor Rugby");

            Team team1 = new Team("Hornfels Heroes");
            team1.setDateCreated(START_DATE_TIME);
            team1.setSport(sportSupplier.get());
            team1.setLocation(location.get());
            teamRepository.save(team1);

            Team team2 = new Team("Hornfels Villains");
            team2.setDateCreated(START_DATE_TIME);
            team2.setSport(sportSupplier.get());
            team2.setLocation(location.get());
            teamRepository.save(team2);

            Team team3 = new Team("Hornfels Guys");
            team3.setDateCreated(START_DATE_TIME);
            team3.setSport(sportSupplier.get());
            team3.setLocation(location.get());
            teamRepository.save(team3);

            Lineup lineup = new Lineup(activity, user0);
            lineup.setLineupRole(LineupRole.STARTER);
            lineupRepository.save(lineup);


            List<Challenge> challenges = List.of(
                    new Challenge(user2, LocalDateTime.of(2030, Month.JANUARY, 1, 0, 0), "New Year Resolution", "Exercise every day", 50),
                    new Challenge(user2, LocalDateTime.of(2030, Month.FEBRUARY, 14, 0, 0), "Valentine's Challenge", "Write a love letter", 30),
                    new Challenge(user2, LocalDateTime.of(2030, Month.MARCH, 17, 0, 0), "Go Green", "Plant a tree", 40),
                    new Challenge(user2, LocalDateTime.of(2030, Month.APRIL, 1, 0, 0), "April Fools", "Pull a harmless prank", 20),
                    new Challenge(user2, LocalDateTime.of(2030, Month.MAY, 1, 0, 0), "Spring Cleaning", "Clean your entire house", 60),
                    new Challenge(user2, LocalDateTime.of(2030, Month.JUNE, 21, 0, 0), "Summer Fun", "Go to the beach", 40),
                    new Challenge(user2, LocalDateTime.of(2030, Month.JULY, 4, 0, 0), "Patriotic Spirit", "Attend a parade", 30),
                    new Challenge(user2, LocalDateTime.of(2030, Month.AUGUST, 26, 0, 0), "Read a Book", "Finish reading a novel", 70),
                    new Challenge(user2, LocalDateTime.of(2030, Month.OCTOBER, 31, 0, 0), "Halloween Fun", "Carve a pumpkin", 40)
            );
            Challenge challenge1 = challenges.get(0);
            challenge1.complete();

            BlogPost challengePost1 = new BlogPost("Exercise every day", "Look at me!", user2, LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0));
            challengePost1.setChallenge(challenge1);
            challengePost1.setDeletable(false);
            challengePost1.setBlogVisibility(BlogVisibility.PUBLIC);

            blogPostRepository.save(challengePost1);

            BlogPost challengePost2 = new BlogPost("Exercise some other time", "eepy", user0, LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0));
            challengePost2.setBlogVisibility(BlogVisibility.PUBLIC);

            blogPostRepository.save(challengePost2);

            challengeRepository.saveAll(challenges);

            List<UserEntity> notifiedUsers = List.of(user0, user1, user2, user3);

            List<Notification> notifications = List.of(
                    new Notification("notification 3", notifiedUsers, user2, null, null,null, LocalDateTime.now().minusDays(1)),
                    new Notification("notification 2", notifiedUsers, user2, null, null,null, LocalDateTime.now()),
                    new Notification("notification 4", notifiedUsers, user2, null, null,null, LocalDateTime.now().minusDays(2)),
                    new Notification("notification 1", notifiedUsers, user2, null, null,null, LocalDateTime.now().plusDays(1)),
                    new Notification("notification 5", notifiedUsers, user2, null, null,null, LocalDateTime.now()),
                    new Notification("notification 6", notifiedUsers, user2, null, null,null, LocalDateTime.now().minusDays(3)),
                    new Notification("notification 7", notifiedUsers, user2, null, null,null, LocalDateTime.now().plusDays(12)),

                    new Notification("shared a challenge!", notifiedUsers, user2, null, null,1L, LocalDateTime.now().plusDays(11)),
                    new Notification("shared a challenge!", notifiedUsers, user2, null, null,2L, LocalDateTime.now().plusDays(13))
            );

            notificationRepository.saveAll(notifications);
        }
    }
}
