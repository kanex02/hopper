package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.controller.api.JsonUser;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.activity.LineupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LineupService class provides methods for managing lineups and retrieving lineup data.
 */
@Service
public class LineupService {
    
    @Autowired
    private LineupRepository lineupRepository;
    
    /**
     * Basic constructor for lineup service
     *
     * @param lineupRepository repository for the lineup service
     */
    public LineupService(LineupRepository lineupRepository) {
        this.lineupRepository = lineupRepository;
    }

    /**
     * Get starting lineup of activity
     *
     * @param activityId the id of the activity
     * @return the starting lineup
     */
    public List<UserEntity> getStartingLineup(Long activityId) {
        return lineupRepository.findStartersByActivityId(activityId);
    }
    
    /**
     * Get substitute lineup of activity
     *
     * @param activityId the id of the activity
     * @return the substitute lineup
     */
    public List<UserEntity> getSubstituteLineup(Long activityId) {
        return lineupRepository.findSubsByActivityId(activityId);
    }

    /**
     * Converts list of users to json format that is acceptable for activity lineup
     * This is needed to prevent the infinite recursion error for json serialization
     *
     * @param users list of users to convert
     * @return list of JsonUser
     */
    public List<JsonUser> convertUserToJsonEquivalent(List<UserEntity> users) {
        
        List<JsonUser> jsonUsers = new ArrayList<>();
        
        for(UserEntity user: users) {
            JsonUser newUser = new JsonUser(user);
            jsonUsers.add(newUser);
        }
        
        return jsonUsers;
    }
    
}
