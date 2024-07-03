package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityScore;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityScoreRepository extends CrudRepository<ActivityScore, Long> {

    Optional<ActivityScore> findActivityScoreById(Long id);

    Optional<ActivityScore> findActivityScoreByActivity_Id(Long id);

    Optional<ActivityScore> findActivityScoreByActivity(Activity activity);

    List<ActivityScore> findAll();
}
