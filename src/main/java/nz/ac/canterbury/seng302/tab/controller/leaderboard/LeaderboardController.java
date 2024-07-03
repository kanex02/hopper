package nz.ac.canterbury.seng302.tab.controller.leaderboard;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.pojo.TopHopperDTO;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.leaderboard.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


import java.util.List;

@Controller
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserService userService;

    private static final String ACTIVE_TAB = "activeTab";

    private static final String LEADERBOARD = "leaderboard";
    private static final String CURRENT_USER = "currentUser";

    @GetMapping("/leaderboard/weekly")
    public String getWeeklyLeaderboard(Model model) {

        UserEntity user = userService.getLoggedInUser();
        List<TopHopperDTO> rows = leaderboardService.getTopHoppersForLastWeek();
        TopHopperDTO userAsDTO = leaderboardService.getUserAsTopHopperForLastWeek(user);
        if (!rows.contains(userAsDTO)){
            model.addAttribute(CURRENT_USER, userAsDTO);
        }
        model.addAttribute(ACTIVE_TAB, "weekly");
        model.addAttribute("rows", rows);

        return LEADERBOARD;
    }

    @GetMapping("/leaderboard/monthly")
    public String getMonthlyLeaderboard(Model model) {

        UserEntity user = userService.getLoggedInUser();
        List<TopHopperDTO> rows = leaderboardService.getTopHoppersForLastMonth();
        TopHopperDTO userAsDTO = leaderboardService.getUserAsTopHopperForLastMonth(user);
        if (!rows.contains(userAsDTO)){
            model.addAttribute(CURRENT_USER, userAsDTO);
        }
        model.addAttribute(ACTIVE_TAB, "monthly");
        model.addAttribute("rows", rows);

        return LEADERBOARD;
    }

    @GetMapping("/leaderboard/all-time")
    public String getAllTimeLeaderboard(Model model) {

        UserEntity user = userService.getLoggedInUser();
        List<TopHopperDTO> rows = leaderboardService.getTopHoppersForAllTime();
        TopHopperDTO userAsDTO = leaderboardService.getUserAsTopHopperForAllTime(user);
        if (!rows.contains(userAsDTO)){
            model.addAttribute(CURRENT_USER, userAsDTO);
        }
        model.addAttribute(ACTIVE_TAB, "all-time");
        model.addAttribute("rows", rows);

        return LEADERBOARD;
    }
}
