package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SearchService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Controller for all profiles page
 * Maps /all-profiles and /user/{id}
 */
@Controller
public class AllProfilesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllProfilesController.class);

    private static final int MIN_SEARCH_SIZE_IF_PRESENT = 3;

    @Autowired
    private UserService userService;

    @Autowired
    private SportService sportService;
    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private Environment env;
    
    private SearchService searchService = new SearchService();

    /**
     * Retrieves a paginated list of all users and displays them
     *
     * @param model the Model object used to store attributes for the view
     * @param page  the Optional object containing the current page number for pagination
     * @param size  the Optional object containing the number of items to display per page for pagination
     * @return thymeleaf allProfiles
     */
    @GetMapping("/all-profiles")
    public String allProfiles(
            Model model,
            HttpServletRequest request,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("name") Optional<String> name,
            @RequestParam(value = "sports", required = false) String sports,
            @RequestParam(value = "cities", required = false) String cities,
            @RequestParam("selectedOptions") Optional<String[]> options,
            RedirectAttributes redirectAttributes
    ) {

        LOGGER.info("GET /all-profiles");

        if(sports != null) {
            List<String> selectedSportsList = Arrays.asList(sports.split(","));
            model.addAttribute("selectedSports", selectedSportsList);
        }
        
        if(cities != null) {
            List<String> selectedCitiesList = Arrays.asList(cities.split(","));
            model.addAttribute("selectedCities", selectedCitiesList);
        }
    
        String baseUrl = env.getProperty("app.baseUrl");
        model.addAttribute("baseUrl", baseUrl);

        model.addAttribute("defaultSports", sportService.getAllSportsNames());
        model.addAttribute("cities", locationService.getAllCityNames());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        if (name.isPresent() && name.get().trim().length() < MIN_SEARCH_SIZE_IF_PRESENT) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your query is too short, please" +
                    String.format(" enter at least %d characters!", MIN_SEARCH_SIZE_IF_PRESENT));
            return "redirect:/all-profiles";
        }
    
        if (name.isPresent() && searchService.containsEmoji(name.get())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Emojis are not allowed in the search input!");
            return "redirect:/all-profiles";
        }

        Page<UserEntity> userEntityPage = userService.findPaginated(
                PageRequest.of(currentPage - 1, pageSize),
                name.orElse(""), sports, cities
        );

        name.ifPresent(s -> model.addAttribute("name", s));

        model.addAttribute("userPage", userEntityPage);

        UserEntity user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        if (userEntityPage != null) {
            int totalPages = userEntityPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .toList();
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }

        return "allProfiles";
    }


    /**
     * Retrieves the details of a user and displays them on the dynamic url
     *
     * @param model    the Model object used to store attributes for the view
     * @param id       the ID of the UserEntity to retrieve
     * @param referrer the previous url
     * @return thymeleaf userDetails
     */
    @GetMapping("/user/{id}")
    public String userProfile(
            Model model,
            @PathVariable("id") Long id,
            @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer
    ) {
        LOGGER.info("GET /user/{}", id);

        // Add form model
        Map<String, String> formModel = new HashMap<>();
        formModel.put("textInput", "");
        model.addAttribute("formModel", formModel);

        // Get viewed user
        UserEntity selectedUser = userService.getUserById(id);
        if (selectedUser == null) {
            throw new IllegalArgumentException("No such user found.");
        }

        // Get logged-in user
        UserEntity loggedInUser = userService.getLoggedInUser();

        // Check if viewing own page
        if (loggedInUser.equals(selectedUser)) {
            return "redirect:/user";
        }

        // Check if the logged-in user is following the selected user
        boolean isFollowed = userService.isUserFollowing(loggedInUser, selectedUser);

        // Add attributes to the model
        model.addAttribute("profileUser", selectedUser);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("notViewingOwnPage", true);
        model.addAttribute("isFollowed", isFollowed);
        model.addAttribute("allTeams", selectedUser.getAllTeams());
        if (selectedUser.getLocation() == null) {
            model.addAttribute("location", new Location());
        }
        else {
            model.addAttribute("location", selectedUser.getLocation());
        }

        if (referrer != null) {
            model.addAttribute("prevUrl", referrer);
        }

        return "userProfilePage";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleIllegalArgumentException(HttpServletRequest req, Exception e) {
        LOGGER.error("Request: {} raised {}", req.getRequestURL(), e);
        return "error/404";
    }

}
