package nz.ac.canterbury.seng302.tab.service;

import jakarta.persistence.EntityNotFoundException;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.ActivityType;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;
import nz.ac.canterbury.seng302.tab.entity.lineup.LineupRole;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.activity.LineupRepository;
import nz.ac.canterbury.seng302.tab.service.activity.ActivityStatisticFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

/**
 * ActivityService class provides methods for managing activities and retrieving activity data.
 */
@Service
public class ActivityService implements PaginatedService<Activity> {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineupRepository lineupRepository;

    @Autowired
    private ActivityStatisticFactories factories;
    private static final String START_TIME = "startTime";

    private static final Sort startTimeSort = Sort.by(START_TIME);

    public ActivityService() {
    }

    /**
     * Constructor for activity service class
     *
     * @param activityRepository The activity repository to be used by the service.
     */
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Sets the lineup repository for the service class
     *
     * @param lineupRepository lineup repository to set for the service
     */
    public void setLineupRepository(LineupRepository lineupRepository) {
        this.lineupRepository = lineupRepository;
    }

    /**
     * Parses and records some raw data about an event statistic into the given activity.
     *
     * @param activity The activity to record the event into
     * @param user     The user attempting to make the request
     * @param type     The type of event to be parsed. Should be a key value of {@link ActivityStatisticFactories }
     * @param json     The raw json form data to be parsed
     * @return Returns the updated activity if successful. The previous value in {@code activity} should be discarded
     * as the data may have been entirely changed.
     * @throws NoSuchElementException   Thrown if the type is not a known name of a factory
     * @throws IllegalArgumentException Thrown if an error occurred in parsing the data
     * @throws SecurityException        Thrown if the {@code user} is not allowed to edit the activity
     */
    public Activity recordStatistic(
            Activity activity,
            UserEntity user,
            String type,
            Map<String, String> json
    ) throws NoSuchElementException, IllegalArgumentException, SecurityException {

        if (!activity.canEdit(user)) {
            throw new SecurityException(
                    String.format("The user %s cannot edit activity %d", user.getEmail(), activity.getId())
            );
        }
        ActivityEventStatistic<?> stat = factories.parseWithType(type, json);
        activity.addEvent(stat);

        return activityRepository.save(activity);
    }

    /**
     * Gets all the activities associated to a team, sorted by the start time
     *
     * @param teamId The id the team to search by
     * @return Returns a list of the activities found.
     */
    public List<Activity> getActivitiesForTeam(Long teamId) {
        return activityRepository.findByTeamId(teamId, startTimeSort);
    }

    /**
     * Saves an activity to persistence. Use the returned instance for further operations as the save operation might
     * have changed the entity instance completely.
     *
     * @param activity The activity toe save
     * @return Returns the saved instance.
     */
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    /**
     * Retrieves activity based on the activity id
     *
     * @param activityId activity id to retrieve
     * @return activity if exists, else null
     */
    @Nullable
    public Activity getActivityById(Long activityId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        return activity.orElse(null);
    }

    /**
     * Gets all the activities that a user has created, sorted by the start time
     *
     * @param userId The id the user to search by
     * @return Returns a list of the activities found
     */
    public List<Activity> getActivitiesForUser(Long userId) {
        return activityRepository.findByUserId(userId, startTimeSort);
    }

    /**
     * Checks for whether the activity is valid, that aren't already included in the entity.
     *
     * @param activity the activity to check
     * @param user     the creator of the activity
     * @return whether the activity is valid
     */
    public boolean isValid(Activity activity, UserEntity user, BindingResult activityResult) {

        boolean isValid = true;

        Team team = activity.getTeam();
        // Check that the user is authorised to add activities
        if (team != null && (!team.getManagers().contains(user) && !team.getCoaches().contains(user))) {
            activityResult.rejectValue("team", "team.unauthorised", "User is not authorised to add activities to this team");
            isValid = false;
        }

        if (activityResult.hasFieldErrors("description")){
            isValid = false;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime;

        try {
            // Check start time is before end time
            startTime = LocalDateTime.parse(activity.getStartTime());
            endTime = LocalDateTime.parse(activity.getEndTime());
            if (startTime.isAfter(endTime)) {
                activityResult.rejectValue("endTime", "time.afterEnd", "End time must be after start time");
                isValid = false;
            }
        } catch (DateTimeParseException exception) {
            if (!activity.getStartTime().equals("")) {
                activityResult.rejectValue(START_TIME, "time.invalid", "Activity time is in an invalid format");
            }
            if (!activity.getEndTime().equals("")) {
                activityResult.rejectValue("endTime", "time.invalid", "Activity time is in an invalid format");
            }
            isValid = false;
        }

        // Check that the start time is after the creation date of the team
        if (activity.getTeam() != null && startTime != null &&
                startTime.isBefore(LocalDateTime.parse(activity.getTeam().getDateCreated()))) {
            activityResult.rejectValue(START_TIME, "time.beforeTeamCreation", "Activity cannot be before the team was created");
            isValid = false;
        }



        // Check that 'game' and 'friendly' activities have a team
        ActivityType type = activity.getType();
        boolean typeRequiresTeam = type == ActivityType.GAME || type == ActivityType.FRIENDLY;
        if (typeRequiresTeam && team == null) {
            activityResult.rejectValue("team", "team.required", "Team is required for this activity type");
            isValid = false;
        }
        if (type == null) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Retrieves the user's personal activities (activities without team involved)
     *
     * @param userId user to find
     * @return list of personal activities
     */
    public List<Activity> getPersonalActivities(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return activityRepository.findByUserAndTeamIsNull(user.get(), Sort.by(START_TIME));
        } else {
            throw new EntityNotFoundException("User was not found");
        }
    }

    /**
     * Retrieves the team activities grouped by team for the given user
     *
     * @param userId userId of the user
     * @return a map containing teams as keys and their corresponding list of activities as values
     */
    public Map<Team, List<Activity>> getTeamActivitiesGroupedByTeam(Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);

        // Note: A guard clause is not used as SonarLint does not recognize it and still reports a code smell.
        if (user.isPresent()) {
            List<Activity> activities = activityRepository
                    .findDistinctByTeamMembersContainingOrTeamManagersContainingOrTeamCoachesContaining(
                            user.get(), user.get(), user.get(),
                            startTimeSort);
            Map<Team, List<Activity>> activitiesByTeam = new HashMap<>();

            for (Activity activity : activities) {
                Team team = activity.getTeam();
                activitiesByTeam.computeIfAbsent(team, k -> new ArrayList<>()).add(activity);
            }

            return activitiesByTeam;
        } else {
            throw new EntityNotFoundException("User was not found");
        }
    }

    /**
     * Paginates the user activities, combining personal activities and team activities grouped by team
     *
     * @param pageable      the pageable object defining the pagination parameters
     * @param userActivites the list of personal activities of the user
     * @param teamListMap   the map containing teams as keys and their corresponding list of activities as values
     * @return a page containing the paginated list of activities
     */
    public Page<Activity> paginateUserActivities(Pageable pageable, List<Activity> userActivites,
                                                 Map<Team, List<Activity>> teamListMap) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Activity> allActivities = new ArrayList<>(userActivites);

        List<Team> teams = new ArrayList<>(teamListMap.keySet());
        teams.sort(Comparator.comparing(Team::getTeamName, String.CASE_INSENSITIVE_ORDER));
        for (Team team : teams) {
            List<Activity> teamActivities = teamListMap.get(team);
            allActivities.addAll(teamActivities);
        }

        boolean paginate = allActivities.size() > 20;

        // Paginate the activities if necessary
        if (paginate) {
            int maxPageSize = 10;
            pageSize = Math.min(pageSize, maxPageSize);

            List<Activity> itemsList;
            if (allActivities.size() >= startItem) {
                int toIndex = Math.min(startItem + pageSize, allActivities.size());
                itemsList = allActivities.subList(startItem, toIndex);
            } else {
                itemsList = Collections.emptyList();
            }

            return new PageImpl<>(itemsList, PageRequest.of(currentPage, pageSize), allActivities.size());

        } else {
            if (allActivities.isEmpty()) {
                return new PageImpl<>(Collections.emptyList());
            } else {
                return new PageImpl<>(allActivities, PageRequest.of(0, allActivities.size()), allActivities.size());
            }
        }
    }


    /**
     * Converts a string representation of a list into a List of Longs.
     *
     * @param stringList the String representation of a list of Longs
     * @return a List of Longs parsed from the input string. If the input string is "[]", an empty list is returned
     * @throws NumberFormatException if any of the trimmed values cannot be parsed into a Long
     */
    public static List<Long> convertStringToLongList(String stringList) throws NumberFormatException {

        List<Long> list = new ArrayList<>();

        if (!stringList.equals("[]")) {
            stringList = stringList.substring(1, stringList.length() - 1);
            for (String part : stringList.split(",")) {
                list.add(Long.parseLong(part.trim()));
            }
        }

        return list;
    }

    @Override
    public List<Activity> search(String query) {
        if (query.isEmpty()) {
            return activityRepository.findAll();
        } else {
            return getActivitiesForTeam(Long.parseLong(query));
        }
    }

    @Override
    public List<Activity> filterSports(List<Activity> items, String query) {
        return items;
    }

    @Override
    public List<Activity> filterCities(List<Activity> items, String query) {
        return items;
    }

    /**
     * Adds a list of users to an activity lineup. Each user is assigned a specific role in the lineup.
     * This method creates a new Lineup object for each user, sets the role, and then saves it using the LineupService.
     * The newly created Lineup object is then added to the activity.
     *
     * @param activity   the Activity entity where the users will be added to the lineup
     * @param lineupRole the role that each user will have in the lineup (e.g., STARTER, SUB)
     * @param ids        the list of user ids
     */
    public void addPlayersToActivityLineup(Activity activity, LineupRole lineupRole, List<Long> ids) {
        for (Long id : ids) {
            Optional<UserEntity> user = userRepository.findById(id);
            if (user.isPresent()) {
                Lineup lineup = new Lineup(activity, user.get());
                lineup.setLineupRole(lineupRole);
                lineupRepository.save(lineup);
                activity.addLineup(lineup);
            }
        }
    }


    /**
     * Method to reset the lineup for a particular activity.
     * Deletes each lineup associated to the activity id.
     *
     * @param activity to be used to reset lineup.
     */
    public void resetLineup(Activity activity) {
        for (int i = 0; i < activity.getLineups().size(); i++) {
            lineupRepository.deleteByActivityId(activity.getId());
        }

        activity.clearLineup();
    }


    /**
     * Gets a user's activities.
     * This includes both personal and team activities.
     *
     * @param user the user to find activities for
     * @return A list of all activities a user is involved in, sorted by start date (earliest to latest).
     */
    public List<Activity> getAllActivitiesForUser(UserEntity user) {
        List<Activity> allActivities = new ArrayList<>(this.getPersonalActivities(user.getId()));
        Map<Team, List<Activity>> teamActivities = this.getTeamActivitiesGroupedByTeam(user.getId());
        for (List<Activity> activities : teamActivities.values()) {
            allActivities.addAll(activities);
        }
        allActivities.sort(Activity::compareByDate);
        return allActivities;
    }

    /**
     * Gets all events relating to the user with a start date that has not yet passed.
     *
     * @param user The user to get activities for
     * @return A list of all activities a user is involved in, sorted by start date (earliest to latest).
     */
    public List<Activity> getAllUpcomingActivitiesForUser(UserEntity user) {
        List<Activity> activities = this.getAllActivitiesForUser(user);
        return activities.stream().filter(
                activity -> LocalDateTime.parse(activity.getStartTime()).isAfter(LocalDateTime.now())
        ).toList();
    }

    /**
     * Method to get the count of all activities.
     *
     * @return the count of all activities.
     */
    public long getCount() {
        return activityRepository.count();
    }



    /**
     * Checks if required Location fields are present if a location is submitted
     *
     * @param location the location to check
     * @param locationResult the binding result to add errors to
     * @return true if the location is valid, false otherwise
     */
    public boolean isLocationValid(Location location, BindingResult locationResult) {
        if (location == null) return true;

        Boolean anyFieldNotEmpty = Stream.of(
                        location.getAddressLine1(),
                        location.getAddressLine2(),
                        location.getSuburb(),
                        location.getPostcode(),
                        location.getCity(),
                        location.getCountry())
                .anyMatch(s -> s != null && !s.isEmpty());

        if (Boolean.TRUE.equals(anyFieldNotEmpty)) {
            boolean addressLine1Invalid = location.getAddressLine1() == null || location.getAddressLine1().trim().isEmpty();
            boolean postcodeInvalid = location.getPostcode() == null || location.getPostcode().trim().isEmpty();
            boolean cityInvalid = location.getCity() == null || location.getCity().trim().isEmpty();
            boolean countryInvalid = location.getCountry() == null || location.getCountry().trim().isEmpty();

            if (addressLine1Invalid || postcodeInvalid || cityInvalid || countryInvalid) {

                if (addressLine1Invalid) {
                    locationResult.rejectValue("addressLine1", "addressLine1.empty", "Address line 1 is required when other location details are provided.");
                }
                if (postcodeInvalid) {
                    locationResult.rejectValue("postcode", "postcode.empty", "Postcode is required when other location details are provided.");
                }
                if (cityInvalid) {
                    locationResult.rejectValue("city", "city.empty", "City is required when other location details are provided.");
                }
                if (countryInvalid) {
                    locationResult.rejectValue("country", "country.empty", "Country is required when other location details are provided.");
                }
                return false;
            }
        }
        return true;
    }

}
