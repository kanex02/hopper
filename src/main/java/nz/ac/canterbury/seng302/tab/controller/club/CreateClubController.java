package nz.ac.canterbury.seng302.tab.controller.club;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Controller for the create club page
 * This controller is responsible for handling requests to "/club/create"
 */
@Controller
public class CreateClubController {
    private static final String CLUB_FORM_PAGE = "club/createClub.html";

    @Autowired
    private UserService userService;
    
    @Autowired
    private SportService sportService;
    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private ClubService clubService;
    
    @Autowired
    private MediaService mediaService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateClubController.class);
    
    /**
     * Handles GET requests to "/club/create", populating the model with necessary data for
     * the club creation view.
     *
     * @param model a model which holds the data for the view
     * @param club an instance of the Club entity
     * @param location an instance of the Location entity
     * @return a string representing the path of the view to be rendered
     */
    @GetMapping("/club/create")
    public String createClub(Model model, Club club, Location location) {
        
        LOGGER.info("GET /club/create");
    
        populateModel(model, club);

        return CLUB_FORM_PAGE;
    }
    
    /**
     * Handles POST requests to "/club/create", processing the form submission for
     * creating a new club.
     *
     * @param club an instance of the Club entity with data from the form
     * @param clubResult results of the binding for the club entity
     * @param location an instance of the Location entity with data from the form
     * @param locationResult results of the binding for the location entity
     * @param teamIds list of team IDs selected for the club
     * @param sportId ID of the selected sport for the club
     * @param file the uploaded image for the club
     * @param model a model which holds the data for the view
     * @return a string representing the path of the view to be rendered
     * @throws IOException if an error occurs during file processing
     */
    @PostMapping("/club/create")
    public String form(@Valid Club club,
                       BindingResult clubResult,
                       @Valid Location location,
                       BindingResult locationResult,
                       @RequestParam(value = "team", required = false) List<Long> teamIds,
                       @RequestParam(value = "sport", required = false) String sportId,
                       @RequestParam(value = "imageUpload", required = false) MultipartFile file,
                       Model model) throws IOException {
    
        LOGGER.info("POST /club/create");
        boolean sportValid = false;
        model.addAttribute("selectedSport", sportId);
        model.addAttribute("selectedTeams", teamIds);
        
        Sport sport = clubService.validateSport(sportId, club, model);
        
        if (sport != null || sportId == null) {
            sportValid = true;
        }

        if (locationResult.hasErrors() || clubResult.hasErrors() || !sportValid) {
            populateModel(model, club);
            return CLUB_FORM_PAGE;
        }
        
        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Location savedLocation = locationService.addLocation(location);
        
        clubService.addTeamsToClub(club, teamIds, sport, user);
        club.setLocation(savedLocation);
        club.setCreator(user);
        clubService.addClub(club);
        
        if (file != null && !file.isEmpty()) {
            try {
                mediaService.setClubImageIfValid(file, club);
            } catch (MediaService.UploadFailure uploadFailure) {
                if (Objects.requireNonNull(uploadFailure.getReason()) == MediaService.FailReason.INVALID_IMAGE) {
                    model.addAttribute("imageError", "Invalid file type. Please upload an image file.");
                    populateModel(model, club);
                    return CLUB_FORM_PAGE;
                } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                    model.addAttribute("imageError", "Error uploading file: "
                            + uploadFailure.getMessage());
                    clubService.deleteById(club.getId());
                    populateModel(model, club);
                    return CLUB_FORM_PAGE;
                }
                
            }
            clubService.addClub(club);
        }
        
        return "redirect:/club/" + club.getId().toString();
        
    }
    
    /**
     * Populates the provided model with necessary data
     *
     * @param model a model which holds the data for the view
     * @param club an instance of the Club entity
     */
    public void populateModel(Model model, Club club) {
        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Team> teams = user.getManagedTeams().stream()
                            .distinct()
                            .toList();
        Set<Sport> sports = sportService.getAllSports();
    
        model.addAttribute("teams", teams);
        model.addAttribute("userProfilePicture", user);
        model.addAttribute("club", club);
        model.addAttribute("sports", sports);
    }
}
