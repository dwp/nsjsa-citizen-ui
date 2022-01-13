package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CounterFormBackUrlRuleTest {

    private static final String IDENTIFIER = "form/current-work/details/is-work-paid";
    private CounterFormBackUrlRule sut = new CounterFormBackUrlRule();
    private StepInstance stepInstance;

    @Test
    public void applyRuleReturnsUrlWithCounter() {
        givenStepInstanceIs(IDENTIFIER, 2);

        String actualUrl = whenRuleIsApplied();

        assertThat(actualUrl, is("/form/current-work/details/2/is-work-paid"));
    }

    @Test
    public void applyRuleReturnsNullWithZeroCounter() {
        givenStepInstanceIs(IDENTIFIER, 0);

        String returnedUrl = whenRuleIsApplied();

        assertThat(returnedUrl, nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyRuleThrowsErrorWithNullStepInstance() {
        givenStepInstanceIs(null, 2);

        whenRuleIsApplied();
    }

    private String whenRuleIsApplied() {
        return sut.apply(stepInstance);
    }

    private void givenStepInstanceIs(final String identifier, final int counter) {
        stepInstance = new StepInstance(new Step(identifier, null, null, Section.NONE), counter, false, false, false);
    }
}
