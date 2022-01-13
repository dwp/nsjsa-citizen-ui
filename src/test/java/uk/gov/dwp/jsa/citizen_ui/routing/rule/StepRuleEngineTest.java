package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepBuilder;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Arrays;
import java.util.function.BiFunction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class StepRuleEngineTest {

    private static final String STEP_IDENTIFIER = "STEP_IDENTIFIER";
    private static final String ERROR_PATH = "form/error404";
    private static final String PATH = "PATH";
    private static final Step STEP = new StepBuilder().withId(STEP_IDENTIFIER).build();
    private static final StepInstance STEP_INSTANCE = new StepInstance(false, false, STEP);
    private static final Step NULL_STEP = null;
    private static final StepInstance STEP_INSTANCE_WITH_NULL_STEP = new StepInstance(false, false, NULL_STEP);

    @Mock
    private BiFunction<RoutingSteps, StepInstance, String> rule;

    @Mock
    private RoutingSteps routingSteps;

    private StepRuleEngine stepRuleEngine;
    private String path;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void returnsErrorForNullRoute() {
        givenARuleEngine();
        whenIGetPath();
        thenThePathIs(ERROR_PATH);
    }

    @Test
    public void returnsPathForRoute() {
        givenARuleEngine();
        when(rule.apply(routingSteps, STEP_INSTANCE)).thenReturn(PATH);
        whenIGetPath();
        thenThePathIs(PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNext_doesNotAcceptNullCurrentStep() {
        givenARuleEngine();
        stepRuleEngine.getNext(routingSteps, STEP_INSTANCE_WITH_NULL_STEP);
    }

    private void givenARuleEngine() {
        stepRuleEngine = new StepRuleEngine(Arrays.asList(rule));
    }

    private void whenIGetPath() {
        path = stepRuleEngine.getNext(routingSteps, STEP_INSTANCE);
    }

    private void thenThePathIs(final String expectedPath) {
        assertThat(path, is(expectedPath));
    }

}
