package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GuardQuestionRuleTest {

    private GuardQuestionRule sut;

    @Mock
    private RoutingSteps routingSteps;

    @Before
    public void createSut() {
        sut = new GuardQuestionRule();
    }

    @Test
    public void callGuardQuestionRule_returnsAlternativeNextStep() {
        String expected = "next step";
        Step step = createStep(expected);
        StepInstance stepInstance = new StepInstance(true, true, step);
        String path = sut.apply(routingSteps, stepInstance);
        assertThat(path, is(expected));
    }

    private Step createStep(final String alternateNextStep) {
        return new Step("id", null, alternateNextStep, Section.NONE);
    }

    private class FormWithGuardedConditionTrue extends AbstractForm {
        @Override
        public Question getQuestion() {
            return null; // not needed here
        }

        @Override
        public void setQuestion(Question question) {
            // not needed here
        }

        @Override
        public boolean isAGuard() {
            return true;
        }

        @Override
        public boolean isGuardedCondition() {
            return true;
        }
    }
}
