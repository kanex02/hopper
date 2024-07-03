package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
public class TeamActivityController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @GetMapping("/activity/team/{teamId}")
    public String getTeamActivitiesPage(Model model,
                                        @PathVariable("teamId") Long teamId,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);


        if(teamService.getTeamById(teamId).isEmpty()) {
            return "redirect:/teams";
        }

        Page<Activity> activityEntityPage = activityService.findPaginated(
                PageRequest.of(currentPage - 1, pageSize),
                teamId.toString(), "", ""
        );

        model.addAttribute("activityPage", activityEntityPage);

        int totalPages = activityEntityPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }

        UserEntity user = userService.getLoggedInUser();

        model.addAttribute("user", user);
        model.addAttribute("activityList", activityService.getActivitiesForTeam(teamId));
        model.addAttribute("team", teamService.getTeamById(teamId));
        return "activity/viewTeamActivities";
    }
}
