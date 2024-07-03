package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ActivityEventStatistic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GenericEventStatisticRepository extends CrudRepository<ActivityEventStatistic<?>, Long> {

    Optional<ActivityEventStatistic<?>> findActivityEventStatisticById(Long id);

    List<ActivityEventStatistic<?>> findAllByActivity_Id(Long id);

    List<ActivityEventStatistic<?>> findAllByActivity_OrderByTimeAsc(Activity activity);

    List<ActivityEventStatistic<?>> findAll();
}

