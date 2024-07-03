package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.FactStatistic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FactStatisticRepository extends CrudRepository<FactStatistic, Long> {

    Optional<FactStatistic> findFactStatisticById(Long id);

    List<FactStatistic> findAllByActivity_Id(Long id);

    List<FactStatistic> findAllByActivity_OrderByTimeAsc(Activity activity);

    List<FactStatistic> findAll();
}
