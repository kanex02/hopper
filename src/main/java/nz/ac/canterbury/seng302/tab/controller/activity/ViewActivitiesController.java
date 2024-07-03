package nz.ac.canterbury.seng302.tab.controller.activity;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Controller for the view activities page
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@Controller
public class ViewActivitiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewActivitiesController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    /**
     * Gets viewActivities page to be displayed
     * @param model The {@link Model} of the page
     * @param userId id of the user whose activities we are retrieving
     * @param page page number for pagination
     * @param size size of the pagination
     * @return returns the thymeleaf view activities page
     */
    @GetMapping({"/activity/view/{userId}"})
    public String getActivitiesPage(Model model,
                                    @PathVariable("userId") Long userId,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size) {
        LOGGER.info("GET /activity/view/{id}");


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);

        UserEntity existingUser = userService.getLoggedInUser();

        // If user doesn't exist
        if (existingUser == null) {
            return "redirect:/";
        }

        // If id in path doesn't match user's id
        if (!userId.equals(existingUser.getId())) {
            return String.format("redirect:/activity/view/%s", existingUser.getId());
        }

        List<Activity> personalActivities = activityService.getPersonalActivities(userId);
        Map<Team, List<Activity>> activitiesByTeam = activityService.getTeamActivitiesGroupedByTeam(userId);



        Page<Activity> activityEntityPage = activityService.paginateUserActivities(PageRequest.of(currentPage - 1,
            pageSize), personalActivities, activitiesByTeam);

        model.addAttribute("activityPage", activityEntityPage);

        int totalPages = activityEntityPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("user", existingUser);
        model.addAttribute("userId", userId);
        model.addAttribute("imageUrl", existingUser.getProfilePicturePath());

        return "activity/viewActivitiesPage";
    }

}
