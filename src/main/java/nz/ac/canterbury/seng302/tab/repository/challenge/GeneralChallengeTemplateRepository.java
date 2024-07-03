package nz.ac.canterbury.seng302.tab.repository.challenge;

import nz.ac.canterbury.seng302.tab.entity.challenge.GeneralChallengeTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeneralChallengeTemplateRepository extends CrudRepository<GeneralChallengeTemplate, Long> {

    List<GeneralChallengeTemplate> findAll();

    default boolean isEmpty() {
        return count() == 0;
    }
}
