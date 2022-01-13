package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.BackStepRuleEngine;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.StepRuleEngine;

import java.util.Map;
import java.util.Optional;

@Primary
@Service
public class RoutingServiceImpl implements RoutingService {

    private final StepHistoryService stepHistoryService;
    private final StepRuleEngine stepRuleEngine;
    private final BackStepRuleEngine backStepRuleEngine;
    private final RoutingSteps routingSteps;

    @Autowired
    public RoutingServiceImpl(final StepHistoryService stepHistoryService,
                              final StepRuleEngine stepRuleEngine,
                              final BackStepRuleEngine backStepRuleEngine,
                              final RoutingSteps routingSteps) {
        this.stepHistoryService = stepHistoryService;
        this.stepRuleEngine = stepRuleEngine;
        this.backStepRuleEngine = backStepRuleEngine;
        this.routingSteps = routingSteps;
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
        Optional<StepInstance> lastStep = stepHistoryService.getLastStep(claimId);
        if (lastStep.isPresent()) {
            Optional<StepInstance> lastStepOptional = stepHistoryService.getLastStep(claimId);
            if (lastStepOptional.isPresent()) {
                return backStepRuleEngine.getBack(lastStepOptional.get());
            }
        }
        return "/";
    }

    public void arrivedOnPage(final String claimId, final StepInstance stepInstance) {
        stepHistoryService.arrivedOnPage(claimId, stepInstance);
    }

    public void leavePage(final String claimId, final StepInstance stepInstance) {
        stepHistoryService.registerStep(claimId, stepInstance);
    }

    public Optional<StepInstance> getLastGuard(final String claimId, final StepInstance currentStepInstance) {
        return stepHistoryService.getLastGuard(claimId, currentStepInstance);
    }

    public void clearHistory(final String claimId) {
        stepHistoryService.clearHistory(claimId);
    }

    public void clearSummaryHistory(final String claimId) {
        stepHistoryService.clearSummaryHistory(claimId);
    }

    public Map<String, String> getKeyValuePairForPageTitles() {
        return routingSteps.getKeyValuePairForPageTitles();
    }
}
