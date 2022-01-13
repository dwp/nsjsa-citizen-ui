package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.repository.StackStepHistoryRepository;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.StepRuleEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RoutingServiceInMemory implements RoutingService {
    private final StepHistoryService stepHistoryService;
    private final StepRuleEngine stepRuleEngine;
    private final RoutingSteps routingSteps;
    private final StackStepHistoryRepository stackStepHistoryRepository;
    private final Map<String, StackStepHistory> stackStepHistories;

    @Autowired
    public RoutingServiceInMemory(final StepHistoryService stepHistoryService,
                                  final StepRuleEngine stepRuleEngine,
                                  final RoutingSteps routingSteps,
                                  final StackStepHistoryRepository stackStepHistoryRepository) {
        this.stepHistoryService = stepHistoryService;
        this.stepRuleEngine = stepRuleEngine;
        this.routingSteps = routingSteps;
        this.stackStepHistoryRepository = stackStepHistoryRepository;
        this.stackStepHistories = new HashMap<>();
    }

    public String getNext(final StepInstance stepInstance) {
        return stepRuleEngine.getNext(routingSteps, stepInstance);
    }

    public void registerStep(final Step step) {
        routingSteps.register(step);
    }

    public void deregisterStep(final Step step) {
        routingSteps.deregister(step);
    }

    public Optional<Step> getStep(final String identifier) {
        return routingSteps.getStep(identifier);
    }

    public String getBackRef(final String claimId) {
        // We do not need the back ref functionality to restore the claim.
        throw new UnsupportedOperationException();
    }

    public void arrivedOnPage(final String claimId, final StepInstance stepInstance) {
        getStackStepHistory(claimId).arrivedOnPage(stepInstance);
    }

    public void leavePage(final String claimId, final StepInstance stepInstance) {
        getStackStepHistory(claimId).registerStep(stepInstance);
    }

    public Optional<StepInstance> getLastGuard(final String claimId, final StepInstance currentStepInstance) {
        return getStackStepHistory(claimId).getLastGuard(currentStepInstance);
    }

    public void clearHistory(final String claimId) {
        // We do not need the back ref functionality to restore the claim.
        throw new UnsupportedOperationException();
    }

    public void clearSummaryHistory(final String claimId) {
        // We do not need the back ref functionality to restore the claim.
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(final String claimId) {
        stackStepHistoryRepository.save(this.stackStepHistories.get(claimId));
        this.stackStepHistories.remove(claimId);
    }

    StackStepHistory getStackStepHistory(final String claimId) {
        if (!this.stackStepHistories.containsKey(claimId)) {
            final StackStepHistory stackStepHistory = this.stepHistoryService.getOrCreateStackStepHistory(claimId);
            this.stackStepHistories.put(claimId, stackStepHistory);
            return stackStepHistory;
        }
        return this.stackStepHistories.get(claimId);
    }

    public Map<String, String> getKeyValuePairForPageTitles() {
        return routingSteps.getKeyValuePairForPageTitles();
    }
}
