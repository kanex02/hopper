package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.SubstitutionStatistic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubstitutionStatisticRepository extends CrudRepository<SubstitutionStatistic, Long> {

    Optional<SubstitutionStatistic> findSubstitutionStatisticById(Long id);

    List<SubstitutionStatistic> findAllByActivity_Id(Long id);

    List<SubstitutionStatistic> findAllByActivity_OrderByTimeAsc(Activity activity);

    List<SubstitutionStatistic> findAll();
}

