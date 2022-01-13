package uk.gov.dwp.jsa.citizen_ui.routing;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

import java.util.Objects;

public class StepInstance {

    private Step step;

    private int counter;

    private boolean isAGuard;

    private boolean isGuardedCondition;

    private EditMode editMode;

    private boolean hasNoGuard;

    public StepInstance() {
        //for redis
    }

    public StepInstance(final boolean isAGuard, final boolean isGuardedCondition, final Step step) {
        this.isAGuard = isAGuard;
        this.isGuardedCondition = isGuardedCondition;
        this.step = step;
        this.hasNoGuard = false;
    }

    public StepInstance(final Step step, final int counter, final boolean isAGuard,
                        final boolean isGuardedCondition, final boolean hasNoGuard) {
        this.step = step;
        this.counter = counter;
        this.isAGuard = isAGuard;
        this.isGuardedCondition = isGuardedCondition;
        this.hasNoGuard = hasNoGuard;
    }

    public void setIsAGuard(final boolean isAGuard) {
        this.isAGuard = isAGuard;
    }

    public void setCounter(final int counter) {
        this.counter = counter;
    }

    public void setIsGuardedCondition(final boolean isGuardedCondition) {
        this.isGuardedCondition = isGuardedCondition;
    }

    public void setStep(final Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    public boolean isAGuard() {
        return isAGuard;
    }

    public boolean isGuardedCondition() {
        return isGuardedCondition;
    }

    public int getCounter() {
        return counter;
    }

    public EditMode getEdit() {
        return editMode;
    }

    public void setEdit(final EditMode editMode) {
        this.editMode = editMode;
    }

    public boolean hasNoGuard() {
        return hasNoGuard;
    }

    public void setHasNoGuard(final boolean hasNoGuard) {
        this.hasNoGuard = hasNoGuard;
    }

    public Integer getClaimKey() {
        return step.getIdentifier().hashCode() ^ counter;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StepInstance that = (StepInstance) o;
        return counter == that.counter
                && isAGuard == that.isAGuard
                && Objects.equals(step.getIdentifier(), that.step.getIdentifier())
                && editMode == that.editMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(step, counter, isAGuard, editMode);
    }
}
