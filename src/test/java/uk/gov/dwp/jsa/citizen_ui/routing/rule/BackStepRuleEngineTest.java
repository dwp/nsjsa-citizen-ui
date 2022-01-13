package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepBuilder;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Arrays;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class BackStepRuleEngineTest {

    private static final String STEP_IDENTIFIER = "STEP_IDENTIFIER";
    private static final String ERROR_PATH = "form/error404";
    private static final String PATH = "PATH";
    private static final String IDENTIFIER = "IDENTIFIER ";
    private static final Step STEP = new StepBuilder().withId(IDENTIFIER).build();
    private static final StepInstance STEP_INSTANCE = new StepInstance(STEP, 0, false, false, false);

    @Mock
    private Function<StepInstance, String> rule;

    private BackStepRuleEngine backStepRuleEngine;
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
        when(rule.apply(STEP_INSTANCE)).thenReturn(PATH);
        whenIGetPath();
        thenThePathIs(PATH);
    }

    private void givenARuleEngine() {
        backStepRuleEngine = new BackStepRuleEngine(Arrays.asList(rule));
    }

    private void whenIGetPath() {
        path = backStepRuleEngine.getBack(STEP_INSTANCE);
    }

    private void thenThePathIs(final String expectedPath) {
        assertThat(path, is(expectedPath));
    }

}
