package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Controller for team view page
 */
@Controller
public class TeamsController {

    Logger logger = LoggerFactory.getLogger(TeamsController.class);

    private static final int MIN_SEARCH_SIZE_IF_PRESENT = 3;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private SportService sportService;

    @Autowired
    private LocationService locationService;
    
    @Autowired
    private Environment env;
    
    private SearchService searchService = new SearchService();

    private static final String REDIRECT_TEAMS = "redirect:/teams/";

    /**
     * Gets teams page to be displayed, also handles searching
     * @param model the model
     * @param teamInput the string to search for
     * @return teams template
     */
    @GetMapping("/teams")
    public String getTemplate(Model model,
                              @RequestParam("teamInput") Optional<String> teamInput,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam(value = "sports", required = false) String sports,
                              @RequestParam(value = "cities", required = false) String cities,
                              RedirectAttributes redirectAttributes) {

        logger.info("GET /teams");

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
    
        String baseUrl = env.getProperty("app.baseUrl");
        model.addAttribute("baseUrl", baseUrl);

        if (sports != null) {
            List<String> selectedSportsList = Arrays.asList(sports.split(","));
            model.addAttribute("selectedSports", selectedSportsList);
        }

        if (cities != null) {
            List<String> selectedCitiesList = Arrays.asList(cities.split(","));
            model.addAttribute("selectedCities", selectedCitiesList);
        }

        if (teamInput.isPresent() && teamInput.get().trim().length() < MIN_SEARCH_SIZE_IF_PRESENT) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your query is too short, please" +
                String.format(" enter at least %d characters!", MIN_SEARCH_SIZE_IF_PRESENT));
            return "redirect:/teams";
        }
    
        if (teamInput.isPresent() && searchService.containsEmoji(teamInput.get().trim())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Emojis are not allowed in the search input!");
            return "redirect:/teams";
        }

        Page<Team> teamEntityPage = teamService.findPaginated(
                PageRequest.of(currentPage - 1, pageSize),
                teamInput.orElse(""), sports, cities
        );

        model.addAttribute("teamPage", teamEntityPage);
        teamInput.ifPresent(s -> model.addAttribute("teamInput", s));

        model.addAttribute("defaultSports", sportService.getAllSportsNames());

        //Get cities based off search
        model.addAttribute("cities", locationService.getAllCityNamesWithTeams(teamService.search(teamInput.orElse(""))));

        int totalPages = teamEntityPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        // querying the database every time is definitely not the best way to do this
        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        redirectAttributes.addFlashAttribute("searchError", "Your query is too short, please enter at least 3 characters!");
        return "teams";
    }

    /**
     * Gets the details of a team from the URL
     * @param teamId ID of the team
     * @param model (map-like) representation of the team to be used by thymeleaf
     * @return team page template
     */
    @GetMapping("/teams/{teamId}")
    public Object getTeam(@PathVariable String teamId, Model model,
                          @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer) {
        try {
            Long id = Long.parseLong(teamId);
            Optional<Team> result = teamService.getTeamById(id);
            if (result.isEmpty()) {
                throw new NoSuchElementException();
            }
            Team team = result.get();
            model.addAttribute("team", team);
            model.addAttribute("location", team.getLocation());
            model.addAttribute("dateCreated", team.getDateCreated().split("T")[0]);
            model.addAttribute("sport", team.getSport());
            model.addAttribute("users", teamService.getSortedUsers(team));
            
            // querying the database every time is definitely not the best way to do this
            UserEntity user = userService.getLoggedInUser();
            model.addAttribute("user", user);
            model.addAttribute("notAuthorized", !team.isManager(user));
    
            model.addAttribute("isFollowed", team.getTeamFollowers().contains(user));
    
            logger.info(referrer);
            if (referrer != null) {
                if (!referrer.contains("teams")) {
                    referrer = null;
                }
                model.addAttribute("prevUrl", referrer);
            }
            return "teamPage";
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team not found");
        }
    }

    /**
     * Will update the roles of the users after submitting changes
     * @param teamId
     * @param formParams
     * @return redirect to team page to show changes
     */
    @PostMapping("/teams/{team_id}/update_roles")
    public String updateRoles(@PathVariable("team_id") Long teamId,
                              Model model,
                              @RequestParam Map<String, String> formParams,
                              @ModelAttribute("_csrf") String csrfToken,
                              RedirectAttributes redirectAttributes) {
        logger.info("POST /teams/{}/update_roles", teamId);

        // delete first formParam as its spring security token
        formParams.remove("_csrf");

        if (Collections.frequency(formParams.values(), "manager") > 3) {
            redirectAttributes.addFlashAttribute("formErrorRoles", "There can only be 3 managers per team!");
            return REDIRECT_TEAMS + teamId;
        }
        if (Collections.frequency(formParams.values(), "manager") < 1) {
            redirectAttributes.addFlashAttribute("formErrorRoles", "There must be at least 1 manager!");
            return REDIRECT_TEAMS + teamId;
        }

        teamService.updateRoles(formParams, teamId);

        redirectAttributes.addFlashAttribute("updatedRoles", true);

        return REDIRECT_TEAMS + teamId;
    }
}
