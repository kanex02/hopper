package nz.ac.canterbury.seng302.tab.controller;


import nz.ac.canterbury.seng302.tab.controller.api.JsonChallenge;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.blog.BlogPostService;
import nz.ac.canterbury.seng302.tab.service.challenge.ChallengeService;
import nz.ac.canterbury.seng302.tab.service.cosmetic.LevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * This is a basic spring boot controller, note the {@link Controller} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 * <p>
 * Code largely based on example provided by Morgan English.
 *
 * @author <a href="https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023/-/tree/master/src/main/java/security/example">Morgan English</a>
 */
@Controller
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ChallengeService challengeService;
    
    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private LevelService levelService;

    @Autowired
    private Environment environment;

    /**
     * Application home
     *
     * @return home page
     */
    @GetMapping("/")
    public String home(Model model) {
        LOGGER.info("GET /");
        UserEntity user = userService.getLoggedInUser();
        if (user == null) {
            long teamCount = teamService.getCount();
            long playerCount = userService.getCount();
            long activityCount = activityService.getCount();
            model.addAttribute("user", user);
            model.addAttribute("teamCount", teamCount);
            model.addAttribute("playerCount", playerCount);
            model.addAttribute("activityCount", activityCount);
            return "landing";
        }
        return "redirect:/home";
    }

    /**
     * The controller for adding attributes to the home page.
     * @param model The {@link Model} of the page
     * @return Template to serve
     */
    @GetMapping("/home")
    public String homePage(Model model) {
    
        if (!model.containsAttribute("blogPost")) {
            model.addAttribute("blogPost", new BlogPost());
        }
        
        UserEntity user = userService.getLoggedInUser();
        List<JsonChallenge> jsonChallenges = challengeService.getAllAvailableChallengesForUser(user)
                .stream()
                .map(challenge -> new JsonChallenge(challenge, user))
                .toList();

        model.addAttribute("activities",
                activityService.getAllUpcomingActivitiesForUser(user));
        model.addAttribute("managedTeams", user.getManagedTeams());
        model.addAttribute("coachedTeams", user.getCoachedTeams());
        model.addAttribute("associatedTeams", user.getAssociatedTeams());
        model.addAttribute("availableChallenges", jsonChallenges);
        model.addAttribute("allPosts", blogPostService.getAllPostsForUser(user));
        model.addAttribute("levels", levelService.getAllLevels());
        model.addAttribute("hopsRequired", user.getTotalHopsRequiredForNextLevel()-user.getTotalHops());
        model.addAttribute("baseUrl", environment.getProperty("app.baseUrl"));
        model.addAttribute("challenges", jsonChallenges);
        return "homePage";
    }

}
