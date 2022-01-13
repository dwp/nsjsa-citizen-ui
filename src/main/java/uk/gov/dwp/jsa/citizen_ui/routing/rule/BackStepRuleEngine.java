package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class BackStepRuleEngine {
    private final List<Function<StepInstance, String>> routingRules;

    public BackStepRuleEngine(final List<Function<StepInstance, String>> routingRules) {
        this.routingRules = routingRules;
    }

    public BackStepRuleEngine() {
        this(Arrays.asList(
                new CounterFormBackUrlRule(),
                new DefaultBackUrlRule()));
    }

    public String getBack(final StepInstance stepInstance) {
        Assert.notNull(stepInstance, "stepInstance");
        for (Function<StepInstance, String> rule : routingRules) {
            String route = rule.apply(stepInstance);
            if (null != route) {
                return route;
            }
        }
        return "form/error404";
    }


}
