package uk.gov.dwp.jsa.citizen_ui.routing;

import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;

public class StepInstanceBuilder {
    private Step step;
    private int counter = 0;
    private boolean isAGuard= false;
    private boolean isGuardedCondition  = false;
    private boolean hasNoGuard  = false;
    private EditMode editMode = EditMode.NONE;

    public StepInstanceBuilder withStep(final Step step) {
        this.step = step;
        return this;
    }

    public StepInstanceBuilder withCounter(final int counter) {
        this.counter = counter;
        return this;
    }

    public StepInstance build() {
        return new StepInstance(step, counter, isAGuard, isGuardedCondition, hasNoGuard);

    }
}
