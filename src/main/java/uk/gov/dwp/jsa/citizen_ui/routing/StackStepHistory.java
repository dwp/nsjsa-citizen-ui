package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;

@RedisHash(value = "StackStepHistory", timeToLive = Constants.REDIS_TTL)
public class StackStepHistory implements StepHistory {

    private Stack<StepInstance> stepHistory = new Stack<>();

    @Id
    private String claimId;

    public StackStepHistory() {
        //redish
    }

    public StackStepHistory(final String claimId) {
        this.claimId = claimId;

    }

    public Stack<StepInstance> getStepHistory() {
        return stepHistory;
    }

    public void setStepHistory(final Stack<StepInstance> stepHistory) {
        this.stepHistory = stepHistory;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(final String claimId) {
        this.claimId = claimId;
    }

    @Override
    public void registerStep(final StepInstance stepInstance) {
        stepHistory.push(stepInstance);
    }

    @Override
    public Optional<StepInstance> getLastStep() {
        if (!stepHistory.isEmpty()) {
            return Optional.of(stepHistory.peek());
        }
        return Optional.empty();
    }

    @Override
    public Optional<StepInstance> getLastGuard(final StepInstance currentStepInstance) {
        if (currentStepInstance.getEdit() != null && !EditMode.NONE.equals(currentStepInstance.getEdit())) {
            Optional<StepInstance> lastGuardForEditMode = getLastGuardForEditMode(currentStepInstance,
                    currentStepInstance.getEdit());
            if (lastGuardForEditMode.isPresent()) {
                return lastGuardForEditMode;
            }
        }
        currentStepInstance.setEdit(null);
        return getLastGuardForEditMode(currentStepInstance, EditMode.NONE);
    }

    private Optional<StepInstance> getLastGuardForEditMode(final StepInstance currentStepInstance,
                                                           final EditMode editmode) {
        Optional<StepInstance> guardStepInstanceOptional = filterHistoryBySectionAndCounter(currentStepInstance,
                editmode)
                .reduce((first, second) -> second);
        if (guardStepInstanceOptional.isPresent()) {
            return guardStepInstanceOptional;
        } else {
            guardStepInstanceOptional = filterHistoryBySection(currentStepInstance, editmode)
                    .reduce((first, second) -> first);
            if (guardStepInstanceOptional.isPresent()) {
                return guardStepInstanceOptional;
            }
        }
        return Optional.empty();
    }

    private boolean guardIsHigherInPosition(final StepInstance currentStepInstance,
                                            final StepInstance guardStepInstanceOptional) {
        int currentStepPosition = stepHistory.search(currentStepInstance);
        return stepHistory.search(guardStepInstanceOptional) > currentStepPosition;
    }

    private Stream<StepInstance> filterHistoryBySectionAndCounter(final StepInstance currentStepInstance,
                                                                  final EditMode editmode) {
        return stepHistory.stream()
                .filter(stepInstance -> isGuardWithinSameSectionAndCounter(stepInstance,
                        currentStepInstance, editmode));
    }

    private Stream<StepInstance> filterHistoryBySection(final StepInstance currentStepInstance,
                                                        final EditMode editMode) {
        return stepHistory.stream()
                .filter(stepInstance -> isGuardWithinSameSection(stepInstance, currentStepInstance, editMode));
    }

    private boolean isGuardWithinSameSectionAndCounter(final StepInstance stepInstance,
                                                       final StepInstance currentStepInstance,
                                                       final EditMode editmode) {
        return isGuardWithinSameSection(stepInstance, currentStepInstance, editmode)
                && currentStepInstance.getCounter() == stepInstance.getCounter();
    }

    private boolean isGuardWithinSameSection(final StepInstance stepInstance,
                                             final StepInstance currentStepInstance,
                                             final EditMode editmode) {
        if (EditMode.NONE.equals(editmode)) {
            return isGuardWithinSameSectionBeforeCurrentInstance(stepInstance, currentStepInstance)
                    && (stepInstance.getEdit() == null || EditMode.NONE.equals(stepInstance.getEdit()));
        } else {
            return isGuardWithinSameSectionBeforeCurrentInstance(stepInstance, currentStepInstance)
                    && (null != stepInstance.getEdit() && !EditMode.NONE.equals(stepInstance.getEdit()));

        }
    }

    private boolean isGuardWithinSameSectionBeforeCurrentInstance(final StepInstance stepInstance,
                                                                  final StepInstance currentStepInstance) {
        return !stepInstance.getStep().equals(currentStepInstance.getStep())
                && currentStepInstance.getStep().getSection().equals(stepInstance.getStep().getSection())
                && stepInstance.isAGuard()
                && guardIsHigherInPosition(currentStepInstance, stepInstance);
    }

    @Override
    public void arrivedOnPage(final StepInstance stepInstance) {
        if (hasTheUserSteppedBackAPage(stepInstance)) {
            stepHistory.pop();
        }
    }

    private boolean hasTheUserSteppedBackAPage(final StepInstance stepInstance) {
        if (stepHistory.empty()) {
            return false;
        }
        return stepInstance.getStep().getIdentifier().equals(stepHistory.peek().getStep().getIdentifier());
    }

    public void clearSummaryHistory() {
        while (stepInstanceIsOfSummaryMode()) {
            stepHistory.pop();
        }
    }

    private boolean stepInstanceIsOfSummaryMode() {
        return !stepHistory.empty() && stepHistory.peek() != null && (stepHistory.peek().getEdit() != null
                && !EditMode.NONE.equals(stepHistory.peek().getEdit()));
    }
}
