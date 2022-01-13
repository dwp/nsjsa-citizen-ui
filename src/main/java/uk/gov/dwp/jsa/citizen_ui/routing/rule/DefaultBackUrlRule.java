package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.function.Function;

import static org.springframework.util.Assert.notNull;

public class DefaultBackUrlRule implements Function<StepInstance, String> {

    @Override
    public String apply(final StepInstance stepInstance) {
        notNull(stepInstance, "Identifier");
        notNull(stepInstance.getStep().getIdentifier(), "Identifier");
        return "/" + stepInstance.getStep().getIdentifier();
    }
}
