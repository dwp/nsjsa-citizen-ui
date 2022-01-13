package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
public class DefaultRuleTest {

    private DefaultRule sut;

    @Mock
    private RoutingSteps routingSteps;

    @Before
    public void createSut() {
        sut = new DefaultRule();
    }

    @Test
    public void callDefaultRule_returnsNextStep() {
        String expected = "next step";
        Step step = createStep(expected);
        StepInstance stepInstance = new StepInstance(false, false, step);

        String path = sut.apply(routingSteps, stepInstance);
        assertThat(path, is(expected));
    }

    @Test(expected = IllegalStateException.class)
    public void callDefaultRule_throwsStateException() {
        Step step = createStep(null);
        StepInstance stepInstance = new StepInstance(false, false, step);
        sut.apply(routingSteps, stepInstance);
    }

    private Step createStep(final String nextStep) {
        return new Step("id", nextStep, null, Section.NONE);
    }
}
