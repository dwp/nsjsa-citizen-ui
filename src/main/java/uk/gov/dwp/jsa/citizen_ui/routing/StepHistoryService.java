package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.repository.StackStepHistoryRepository;

import java.util.Optional;

import static org.thymeleaf.util.StringUtils.isEmpty;

@Component
public class StepHistoryService {

    private StackStepHistoryFactory stackStepHistoryFactory;
    private StackStepHistoryRepository stackStepHistoryRepository;

    @Autowired
    StepHistoryService(final StackStepHistoryFactory stackStepHistoryFactory,
                       final StackStepHistoryRepository stackStepHistoryRepository) {
        this.stackStepHistoryFactory = stackStepHistoryFactory;
        this.stackStepHistoryRepository = stackStepHistoryRepository;
    }


    void registerStep(final String claimId, final StepInstance stepInstance) {
        StackStepHistory stackStepHistory = getOrCreateStackStepHistory(claimId);
        stackStepHistory.registerStep(stepInstance);
        stackStepHistoryRepository.save(stackStepHistory);
    }

    Optional<StepInstance> getLastStep(final String claimId) {
        return getOrCreateStackStepHistory(claimId).getLastStep();

    }

    Optional<StepInstance> getLastGuard(final String claimId, final StepInstance currentStepInstance) {
        return getOrCreateStackStepHistory(claimId).getLastGuard(currentStepInstance);
    }

    void arrivedOnPage(final String claimId, final StepInstance stepInstance) {
        StackStepHistory stackStepHistory = getOrCreateStackStepHistory(claimId);
        stackStepHistory.arrivedOnPage(stepInstance);
        stackStepHistoryRepository.save(stackStepHistory);
    }

    StackStepHistory getOrCreateStackStepHistory(final String claimId) {
        StackStepHistory stackStepHistory = isEmpty(claimId) ? null
                : stackStepHistoryRepository.findById(claimId).orElse(null);
        if (stackStepHistory == null) {
            stackStepHistory = stackStepHistoryFactory.create(claimId);
            stackStepHistoryRepository.save(stackStepHistory);
        }
        return stackStepHistory;
    }


    public void clearHistory(final String claimId) {
        stackStepHistoryRepository.deleteById(claimId);
    }

    public void clearSummaryHistory(final String claimId) {
        StackStepHistory stackStepHistory = getOrCreateStackStepHistory(claimId);
        stackStepHistory.clearSummaryHistory();
        stackStepHistoryRepository.save(stackStepHistory);
    }
}
