package nz.ac.canterbury.seng302.tab.repository.challenge;

import nz.ac.canterbury.seng302.tab.entity.challenge.QuantifiableChallengeFactory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuantifiableChallengeFactoryRepository extends CrudRepository<QuantifiableChallengeFactory, Long> {


    List<QuantifiableChallengeFactory> findAll();

    default boolean isEmpty() {
        return count() == 0;
    }


}
