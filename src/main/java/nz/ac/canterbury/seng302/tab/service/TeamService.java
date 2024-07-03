package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService implements PaginatedService<Team> {

    private final TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    private static final String MEMBER = "member";
    private static final String COACH = "coach";
    private static final String MANAGER = "manager";
    private static final String INVALID_ROLE = "Invalid role: ";

    /**
     * Constructs a new TeamService object with the specified TeamRepository
     *
     * @param teamRepository the TeamRepository to be used by the TeamService
     */
    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Gets all Teams from persistence
     *
     * @return all Teams currently saved in persistence
     */
    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    /**
     * Adds a team to persistence
     *
     * @param team object to persist
     * @return the saved team object
     */
    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    /**
     * Gets a team by its id.
     *
     * @param id id to search.
     * @return the team with the given id or Optional#empty() if none found.
     */
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }


    @Override
    public List<Team> search(String query) {
        // Escape wildcards
        String escapedQuery = query.replace("%", "\\%").replace("_", "\\_");
        return teamRepository.findTeamByString(escapedQuery);
    }

    @Override
    public List<Team> filterSports(List<Team> teams, String query) {
        return query == null ? teams : teams.stream().filter(team -> Arrays.asList(query.split(",")).contains(team.getSport().getName())).toList();
    }

    @Override
    public List<Team> filterCities(List<Team> teams, String query) {
        if (query != null) {
            return teams.stream().filter(team -> Arrays.asList(query.split(",")).contains(team.getLocation().getCity())).toList();
        }
        return teams;
    }

    /**
     * Generates a new token for the given team using the first 12 characters of a random UUID.
     * Regenerates it if it already exists
     *
     * @param team The team for which to generate the new token.
     * @return The string of the new generated token
     */
    public String generateNewToken(Team team) {
        UUID newToken = UUID.randomUUID();
        String token = newToken.toString().replace("-", "").substring(0, 12);
        while (teamRepository.getAllTokens().contains(token)) {
            newToken = UUID.randomUUID();
            token = newToken.toString().replace("-", "").substring(0, 12);
        }
        team.setJoinToken(token);
        teamRepository.save(team);
        return token;
    }

    public void deleteTeamById(Long id) {
        teamRepository.deleteById(id);
    }

    /**
     * Retrieves team object by its associated token
     *
     * @param token the token associated with the team
     * @return the team object with the specified token, or null if no team is found
     */
    public Team getTeamByToken(String token) {
        return teamRepository.findByToken(token);
    }

    /**
     * Adds a user to a team
     *
     * @param user the user object to be added to the team
     * @param team the team object to which tghe user is being added
     * @return true if the user is successfully added to the team, false otherwise
     */
    public boolean addUser(UserEntity user, Team team, String role) {

        if (team == null || user == null) {
            return false;
        }

        if (team.getUsers().contains(user)) {
            return false;
        }

        switch (role) {
            case MEMBER -> team.addMember(user);
            case COACH -> team.addCoach(user);
            case MANAGER -> team.addManager(user);
            default -> throw new IllegalArgumentException(INVALID_ROLE + role);
        }
        teamRepository.save(team);
        return true;
    }

    /**
     * Updates the users role in the team
     *
     * @param teamId   id of team
     * @param rolesMap map of the user id and new role
     */
    public void updateRoles(Map<String, String> rolesMap, Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        List<UserEntity> managersToRemove = new ArrayList<>();
        List<UserEntity> managersToAdd = new ArrayList<>();

        for (Map.Entry<String, String> entry : rolesMap.entrySet()) {
            Long userId = Long.parseLong(entry.getKey());
            String role = entry.getValue();

            UserEntity user = team.getUsers().stream().filter(member -> member.getId().equals(userId)).findFirst().orElseThrow();
            String existingRole = team.getUserRole(user);

            if (Objects.equals(role, existingRole)) {
                continue;
            }

            switch (role) {
                case MANAGER -> {
                    if (!existingRole.equals(MANAGER)) {
                        managersToAdd.add(user);
                    }
                }
                case COACH -> team.addCoach(user);
                case MEMBER -> team.addMember(user);
                default -> throw new IllegalArgumentException(INVALID_ROLE + role);
            }

            switch (existingRole) {
                case MANAGER -> {
                    if (!role.equals(MANAGER)) {
                        managersToRemove.add(user);
                    }
                }
                case COACH -> team.removeCoach(user);
                case MEMBER -> team.removeMember(user);
                default -> throw new IllegalArgumentException(INVALID_ROLE + existingRole);
            }
        }

        // Ensure that the team still has at least one manager and no more than three managers after the proposed changes
        long futureManagerCount = (long) team.getManagers().size() - managersToRemove.size() + managersToAdd.size();
        if (futureManagerCount < 1) {
            throw new IllegalStateException("Team must have at least 1 manager");
        } else if (futureManagerCount > 3) {
            throw new IllegalStateException("Maximum list size exceeded - team already has 3 managers");
        }

        // Now apply the manager changes
        managersToAdd.forEach(team::addManager);
        managersToRemove.forEach(team::removeManager);

        teamRepository.save(team);
    }


    public String joinTeam(String token) {
        if (token.isEmpty()) {
            return "Team invitation token cannot be empty!";
        }

        Team team = getTeamByToken(token);
        if (team == null) {
            return "Team invitation token is invalid!";
        }

        UserEntity user = userService.getLoggedInUser();

        if (!addUser(user, team, MEMBER)) {
            return "You can't join a team you are already in!";
        }

        return null;
    }

    /**
     * Gets the number of teams in the database
     *
     * @return the number of teams in the database
     */
    public long getCount() {
        return teamRepository.count();
    }

    /**
     * Saves a team to the database
     *
     * @param team the team to be saved
     * @return the saved team
     */
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    /**
     * Gets sorted list of users in a team by role, first name, then last name
     *
     * @param team the team to get the users from
     * @return the sorted list of users
     */
    public List<UserEntity> getSortedUsers(Team team) {
        List<UserEntity> users = new ArrayList<>(team.getUsers());
        users.sort(Comparator
                .comparing((UserEntity user) -> getRolePriority(team.getUserRole(user)))    // Priority based on role
                .thenComparing(UserEntity::getFirstName)                                    // Then by first name
                .thenComparing(UserEntity::getLastName)                                     // Finally by last name
        );
        return users;
    }

    /**
     * Gets the priority of a role
     *
     * @param role the role to get the priority of
     * @return the priority of the role
     */
    private int getRolePriority(String role) {
        return switch (role.toLowerCase()) {
            case MANAGER -> 1;
            case COACH -> 2;
            case MEMBER -> 3;
            default -> Integer.MAX_VALUE;
        };
    }

    /**
     * Allows the user to follow or unfollow a team if both exists
     *
     * @param team team to follow / unfollow
     * @param user user to follow / unfollow the team from
     */
    public void followOrUnfollowTeam(Team team, UserEntity user) {

        if (team == null || user == null) {
            return;
        }

        if (team.getTeamFollowers().contains(user)) {
            unfollowTeam(team, user);
        } else {
            followTeam(team, user);
        }
    }

    private void followTeam(Team team, UserEntity user) {
        team.addFollower(user);
        user.followTeam(team);
    }

    private void unfollowTeam(Team team, UserEntity user) {
        team.removeFollower(user);
        user.unfollowTeam(team);
    }

}
