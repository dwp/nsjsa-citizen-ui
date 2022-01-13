package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Optional;
import java.util.function.BiFunction;


public class GuardQuestionRule implements BiFunction<RoutingSteps, StepInstance, String> {

    @Override
    public String apply(final RoutingSteps routingSteps, final StepInstance stepInstance) {
        final Step step = stepInstance.getStep();
        Optional<String> nextAlternateStepIdentifier = step.getNextAlternateStepIdentifier();
        if (stepInstance.isAGuard() && stepInstance.isGuardedCondition() && nextAlternateStepIdentifier.isPresent()) {
            return nextAlternateStepIdentifier.get();
        }
        return null;
    }
}
