package nz.ac.canterbury.seng302.tab.repository.activity;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.activity.Activity;
import nz.ac.canterbury.seng302.tab.entity.activity.stat.ScoreEventStatistic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreEventStatisticRepository extends CrudRepository<ScoreEventStatistic, Long> {

    Optional<ScoreEventStatistic> findScoreEventStatisticById(Long id);

    List<ScoreEventStatistic> findAllByActivity_Id(Long id);

    List<ScoreEventStatistic> findAllByActivity_OrderByTimeAsc(Activity activity);

    List<ScoreEventStatistic> findAllByActivity_AndTeamScoredFor_OrderByTimeAsc(Activity activity, Team team);

    List<ScoreEventStatistic> findAll();
}

