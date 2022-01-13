package uk.gov.dwp.jsa.citizen_ui.routing;

import java.util.Optional;

public interface StepHistory {

    void registerStep(StepInstance step);

    Optional<StepInstance> getLastStep();

    Optional<StepInstance> getLastGuard(final StepInstance currentStepInstance);

    void arrivedOnPage(final StepInstance stepInstance);
}
