package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends CrudRepository<Activity, Long> {
    List<Activity> findByTeamId(Long teamId, Sort sort);

    List<Activity> findByUserId(Long userId, Sort sort);

    List<Activity> findAll();
    
    List<Activity> findByUserAndTeamIsNull(UserEntity user, Sort sort);

    List<Activity> findDistinctByTeamMembersContainingOrTeamManagersContainingOrTeamCoachesContaining(
            UserEntity member, UserEntity manager, UserEntity coach, Sort sort);

    Optional<Activity> findById(Long activityId);

}
