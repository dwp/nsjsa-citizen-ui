package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class StepRuleEngine {
    private final List<BiFunction<RoutingSteps, StepInstance, String>> routingRules;

    public StepRuleEngine(final List<BiFunction<RoutingSteps, StepInstance, String>> routingRules) {
        this.routingRules = routingRules;
    }

    public StepRuleEngine() {
        this(Arrays.asList(
                new GuardQuestionRule(),
                new EditRule(),
                new DefaultRule()));
    }

    public String getNext(final RoutingSteps routingSteps, final StepInstance stepInstance) {
        Assert.notNull(stepInstance, "stepInstance");
        Assert.notNull(stepInstance.getStep(), "step");
        for (BiFunction<RoutingSteps, StepInstance, String> rule : routingRules) {
            String route = rule.apply(routingSteps, stepInstance);
            if (null != route) {
                return route;
            }
        }
        return "form/error404";
    }
}
