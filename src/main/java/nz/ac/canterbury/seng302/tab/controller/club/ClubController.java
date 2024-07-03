package nz.ac.canterbury.seng302.tab.controller.club;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.service.club.ClubService;
import nz.ac.canterbury.seng302.tab.service.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Controller for the club's page
 */
@Controller
public class ClubController {

    @Autowired
    private ClubService clubService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SportService sportService;

    @Autowired
    private MediaService mediaService;
    
    private static final String REDIRECT_CLUB = "redirect:/club/";


    /**
     * Mapping to pass through appropriate fields for the club's page
     *
     * @param id id of the club
     * @param model model for the view
     * @return thymeleaf string of the html page to display
     */
    @GetMapping("/club/{id}")
    public String displayClub(@PathVariable String id,
                              Model model) {
        Club club;
        try {
            club = clubService.findById(Long.valueOf(id));
        } catch (Exception e) {
            return "error/404";
        }
    
        if(club == null) {
            return "error/404";
        }
        
        Set<Sport> sports = sportService.getAllSports();
        UserEntity user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    
        List<Long> clubTeamIds = club.getTeams().stream()
            .map(Team::getId).toList();
        List<Team> teams = user.getManagedTeams();

        model.addAttribute("clubTeamIds", clubTeamIds);
        model.addAttribute("club", club);
        model.addAttribute("sports", sports);
        model.addAttribute("teams", teams);
        model.addAttribute("user", user);
        model.addAttribute("location", club.getLocation());
        model.addAttribute("isFollowed", club.getClubFollowers().contains(user));

        boolean canFollow = true;
        for (Team team : club.getTeams()) {
            if (team.getMembers().contains(user) ||
                team.getCoaches().contains(user) ||
                team.getManagers().contains(user)) {
                canFollow = false;
                break;
            }
        }
        if (user == club.getCreator()) {
            canFollow = false;
        }
        model.addAttribute("canFollow", canFollow);
        
        return "club/clubPage.html";
    }
    
    /**
     * Handles POST request for updating an existing club.
     *
     * @param id                 The club ID from the path variable.
     * @param club               The validated club object from the request parameters.
     * @param clubResult         The binding result object containing potential errors from the validation process.
     * @param teamIds            The list of team IDs from the request parameters.
     * @param file               The image file to be uploaded for the club.
     * @param redirectAttributes The attributes to be redirected to the club view after completion.
     * @return The redirection to the updated club view.
     */
    @PostMapping("/club/{id}")
    public String updateClub(@PathVariable String id,
                             @Valid Club club,
                             BindingResult clubResult,
                             @RequestParam(value = "teams", required = false) List<Long> teamIds,
                             @RequestParam(value = "imageUpload", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        
        Club existingClub = clubService.findById(Long.valueOf(id));
        UserEntity user = userService.getLoggedInUser();
    
        existingClub.removeAllTeams();

        try {
            clubService.addTeamsToClub(existingClub, teamIds, club.getAssociatedSport(), user);
        } catch (IllegalStateException e) {
            FieldError error = new FieldError("club", "club", e.getMessage());
            clubResult.addError(error);
        } catch (Exception e) {
            FieldError error = new FieldError("sport", "sport", e.getMessage());
            clubResult.addError(error);
        }

        
        if(clubResult.hasErrors()) {
            Map<String, String> errorMap = clubResult.getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage, ((s, s2) -> s + " " + s2)));
            
            redirectAttributes.addFlashAttribute("club", club);
            redirectAttributes.addFlashAttribute("hasErrors", true);
            redirectAttributes.addFlashAttribute("clubErrors", errorMap);
            return REDIRECT_CLUB + existingClub.getId().toString();
        }

        if (file != null && !file.isEmpty()) {
            try {
                mediaService.setClubImageIfValid(file, existingClub);
            } catch (MediaService.UploadFailure uploadFailure) {
                if (uploadFailure.getReason() == MediaService.FailReason.INVALID_IMAGE) {
                    redirectAttributes.addFlashAttribute("imageError", "Invalid file type. Please upload an image file.");
                    return REDIRECT_CLUB + existingClub.getId().toString();
                } else if (uploadFailure.getReason() == MediaService.FailReason.UPLOAD_ERROR) {
                    redirectAttributes.addFlashAttribute("imageError", "Error uploading file: "
                            + uploadFailure.getMessage());
                    clubService.deleteById(club.getId());
                    return REDIRECT_CLUB + existingClub.getId().toString();
                }

            }
            clubService.addClub(existingClub);
        }
        
        existingClub.setAssociatedSport(club.getAssociatedSport());
        existingClub.setName(club.getName());
        existingClub.setDescription(club.getDescription());
        
        clubService.addClub(existingClub);
        
        return REDIRECT_CLUB + existingClub.getId().toString();
    }
    
    /**
     * Handles POST request for editing the location of an existing club.
     *
     * @param location           The validated location object from the request parameters.
     * @param bindingResult      The binding result object containing potential errors from the validation process.
     * @param id                 The club ID from the path variable.
     * @param redirectAttributes The attributes to be redirected to the club view after completion.
     * @return The redirection to the updated club view.
     */
    @PostMapping("/club/{id}/edit-location")
    public String editClubLocation(@Valid Location location,
                                   BindingResult bindingResult,
                                   @PathVariable String id,
                                   RedirectAttributes redirectAttributes) {
        Club existingClub = clubService.findById(Long.valueOf(id));
    
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors()
                .stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage));
    
            redirectAttributes.addFlashAttribute("modalId", "myModal");
            redirectAttributes.addFlashAttribute("fieldErrors", errorMap);
            return REDIRECT_CLUB + existingClub.getId().toString();
        }
    
        existingClub.setLocation(location);
        clubService.addClub(existingClub);
    
        return REDIRECT_CLUB + existingClub.getId().toString();
    }
}