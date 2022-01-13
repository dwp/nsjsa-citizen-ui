package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.function.Function;

import static org.springframework.util.Assert.notNull;

public class CounterFormBackUrlRule implements Function<StepInstance, String> {

    @Override
    public String apply(final StepInstance stepInstance) {
        notNull(stepInstance, "stepInstance");
        notNull(stepInstance.getStep(), "step");
        int counter = stepInstance.getCounter();
        if (counter > 0) {
            String identifier = stepInstance.getStep().getIdentifier();
            return addCounterToUrl(counter, identifier);
        } else {
            return null;
        }
    }

    public static String addCounterToUrl(final int counter, final String identifier) {
        String[] split = identifier.split(Constants.SLASH);
        if (split.length > 0) {
            split[split.length - 1] = counter + Constants.SLASH + split[split.length - 1];
        }
        return Constants.SLASH + String.join(Constants.SLASH, split);
    }
}
