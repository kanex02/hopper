package nz.ac.canterbury.seng302.tab.service.club;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.club.Club;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

/**
 * Represents a service for managing Club entities in the application.
 */
@Service
public class ClubService {
    
    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private SportService sportService;

    /**
     * Sets the team service of the club service
     *
     * @param teamService team service to set
     */
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Sets the club repository of the club service
     *
     * @param clubRepository club repository to set
     */
    public void setClubRepository(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * Adds a new club to the database.
     *
     * @param club the club to be added
     * @return the saved club entity
     */
    public Club addClub(Club club) {
        return clubRepository.save(club);
    }
    
    /**
     * Deletes a club from the database by its id.
     *
     * @param id the id of the club to be deleted
     */
    public void deleteById(Long id) {
        clubRepository.deleteById(id);
    }
    
    /**
     * Finds a club in the database by its id.
     *
     * @param id the id of the club to be found
     * @return the found club entity or null if no club with the given id exists
     */
    public Club findById(Long id) {
        Optional<Club> club = clubRepository.findById(id);
        return club.orElse(null);
    }
    
    /**
     * Add teams from a list of long ids to a club
     *
     * @param club club to add the teams to
     * @param teamIds a list of team ids
     * @param currentSport sport to compare the team with
     * @param user the creator of the club
     */
    public void addTeamsToClub(Club club, List<Long> teamIds, Sport currentSport, UserEntity user) {
        
        if(teamIds != null) {
            List<Team> selectedTeams;

            selectedTeams = teamIds.stream()
                    .map(teamService::getTeamById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(team -> team.getManagers().contains(user)).toList();

            selectedTeams.forEach(team -> {
                if (team.getClub() != null) {
                    throw new IllegalStateException("One or more of the selected team already has a club.");
                }
            });

            for (Team team : selectedTeams) {
                if (currentSport == team.getSport()) {
                    club.addTeam(team);
                } else {
                    throw new IllegalArgumentException("One or more of the selected team's sport does not match the club's sport.");
                }
            }
        }
    }
    
    /**
     * Validate the sport and add the sport to the club after validating
     *
     * @param sportId optional sport id selected
     * @param club club to add the sport to
     * @param model model managing the attributes of the view
     * @return true if sport is successfully added to the club, false otherwise
     */
    public Sport validateSport(String sportId, Club club, Model model) {
        
        Sport sport = null;
        
        if (club != null && model != null) {
            try {
                if (sportId != null) {
                    sport = sportService.findById(Long.parseLong(sportId));
                    club.setAssociatedSport(sport);
                }
            } catch (NumberFormatException e) {
                model.addAttribute("sportError", "Sport is invalid!");
            }
        }
        return sport;
    }

    /**
     * Saves a club.
     * @param club The club to save
     * @return The saved club
     */
    public Club save(Club club) {
        return clubRepository.save(club);
    }

        /**
     * Allows the user to follow or unfollow a club if both exists
     *
     * @param club club to follow / unfollow
     * @param user user to follow / unfollow the club from
     */
    public void followOrUnfollowClub(Club club, UserEntity user) {

        if (club == null || user == null) {
            return;
        }

        if (club.getClubFollowers().contains(user)) {
            unfollowClub(club, user);
        } else {
            followClub(club, user);
        }
    }

    private void followClub(Club club, UserEntity user) {
        club.addFollower(user);
        user.followClub(club);
    }

    private void unfollowClub(Club club, UserEntity user) {
        club.removeFollower(user);
        user.unfollowClub(club);
    }
}
