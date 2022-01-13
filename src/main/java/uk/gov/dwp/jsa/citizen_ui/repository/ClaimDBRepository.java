package uk.gov.dwp.jsa.citizen_ui.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;

/**
 * Repository to persist {@link ClaimDB}s to Redis.
 */
@Repository
public interface ClaimDBRepository extends CrudRepository<ClaimDB, String> {
}
