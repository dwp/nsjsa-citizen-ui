package uk.gov.dwp.jsa.citizen_ui.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.jsa.citizen_ui.routing.StackStepHistory;

/**
 * Repository to persist {@link StackStepHistory}s to Redis.
 */
@Repository
public interface StackStepHistoryRepository extends CrudRepository<StackStepHistory, String> {
}
