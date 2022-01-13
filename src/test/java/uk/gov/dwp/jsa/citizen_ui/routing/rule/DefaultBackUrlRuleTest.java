package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepBuilder;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultBackUrlRuleTest {

    private DefaultBackUrlRule sut = new DefaultBackUrlRule();
    private static final String IDENTIFIER = "form/current-work/details/is-work-paid";
    private static final StepInstance NULL_STEP_INSTANCE = null;
    private static final Step STEP = new StepBuilder().withId(IDENTIFIER).build();
    private static final StepInstance STEP_INSTANCE = new StepInstance(STEP, 0, false, false, false);

    @Test
    public void applyReturnsExpectedUrl() {

        String actualUrl = whenRuleIsApplied(STEP_INSTANCE);

        assertThat(actualUrl, is("/form/current-work/details/is-work-paid"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyRuleThrowsErrorWithNullStepInstance() {

        whenRuleIsApplied(NULL_STEP_INSTANCE);
    }

    private String whenRuleIsApplied(final StepInstance stepInstance) {
        return sut.apply(stepInstance);
    }


}
