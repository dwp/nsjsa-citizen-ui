package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.function.BiFunction;


public class DefaultRule implements BiFunction<RoutingSteps, StepInstance, String> {

    @Override
    public String apply(final RoutingSteps routingSteps, final StepInstance stepInstance) {
        final Step step = stepInstance.getStep();
        Assert.state(step.getNextStepIdentifier() != null,
                "Cannot have a form with no next stepInstance");
        return step.getNextStepIdentifier();
    }
}
