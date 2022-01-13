package uk.gov.dwp.jsa.citizen_ui.routing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.BackStepRuleEngine;
import uk.gov.dwp.jsa.citizen_ui.routing.rule.StepRuleEngine;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RoutingServiceStepsTests {

    private static final String IDENTIFIER = "IDENTIFIER";

    private static final Step STEP = new StepBuilder().withId(IDENTIFIER).build();

    private RoutingService routingService;

    @Mock
    private StepHistoryService stepHistoryService;
    @Mock
    private RoutingSteps routingSteps;
    @Mock
    private StepRuleEngine stepRuleEngine;
    @Mock
    private BackStepRuleEngine mockBackStepRuleEngine;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void addsStep() {
        givenARoutingService();
        whenIRegisterAStep(STEP);
        thenTheStepIsFound();
    }

    private void givenARoutingService() {
        routingService = new RoutingServiceImpl(stepHistoryService, stepRuleEngine, mockBackStepRuleEngine,
                routingSteps);
    }
    private void whenIRegisterAStep(final Step step) {
        routingService.registerStep(step);
    }

    private void thenTheStepIsFound() {
        verify(routingSteps).register(STEP);
    }
}
