package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.entity.lineup.Lineup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LineupRepository extends CrudRepository<Lineup, Long> {

    @Query("SELECT l.user FROM Lineup l WHERE l.activity.id = :activityId AND l.role = 'STARTER'")
    List<UserEntity> findStartersByActivityId(@Param("activityId") Long activityId);

    @Query("SELECT l.user FROM Lineup l WHERE l.activity.id = :activityId AND l.role = 'SUB'")
    List<UserEntity> findSubsByActivityId(@Param("activityId") Long activityId);

    @Query("SELECT l.user FROM Lineup  l WHERE l.activity.id = :activityId")
    List<UserEntity> findAllUsers(@Param("activityId") Long activityId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Lineup l WHERE l.activity.id = :activityId")
    void deleteByActivityId(@Param("activityId") Long activityId);

}
