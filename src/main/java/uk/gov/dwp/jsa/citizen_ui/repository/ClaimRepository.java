package uk.gov.dwp.jsa.citizen_ui.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;

/**
 * Repository to persist {@link Claim}s to Redis.
 */
@Repository
public interface ClaimRepository extends CrudRepository<Claim, String> {
}
