package uk.gov.dwp.jsa.citizen_ui.routing;

import java.util.Map;
import java.util.Optional;


public interface RoutingService {

    String getNext(final StepInstance stepInstance);

    void registerStep(final Step step);

    void deregisterStep(final Step step);

    Optional<Step> getStep(final String identifier);

    String getBackRef(final String claimId);

    void arrivedOnPage(final String claimId, final StepInstance stepInstance);

    void leavePage(final String claimId, final StepInstance stepInstance);

    Optional<StepInstance> getLastGuard(final String claimId, final StepInstance currentStepInstance);

    void clearHistory(final String claimId);

    void clearSummaryHistory(final String claimId);

    default void save(final String claimId) {
        // Only used for inMemory.
    }

    Map<String, String> getKeyValuePairForPageTitles();
}
