package uk.gov.dwp.jsa.citizen_ui.routing.rule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.controller.personaldetails.EditMode;
import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.routing.RoutingSteps;
import uk.gov.dwp.jsa.citizen_ui.routing.Step;
import uk.gov.dwp.jsa.citizen_ui.routing.StepInstance;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditRuleTest {

    private EditRule sut;

    @Mock
    private StepInstance stepInstance;

    @Mock
    private RoutingSteps routingSteps;

    @Before
    public void createSut() {
        sut = new EditRule();
    }

    @Test
    public void callEditRuleWithSingle_returnsSummaryPage() {
        String expected = EditRule.FORM_SUMMARY;
        when(stepInstance.getEdit()).thenReturn(EditMode.SINGLE);
        String path = sut.apply(routingSteps, stepInstance);
        assertThat(path, is(expected));
    }

    @Test
    public void callEditRuleWithSameSections_returnsNull() {
        when(stepInstance.getEdit()).thenReturn(EditMode.SECTION);
        when(stepInstance.getStep()).thenReturn(createStep(Section.NONE));
        when(routingSteps.getNextStep(anyString())).thenReturn(Optional.of(createStep(Section.NONE)));
        String path = sut.apply(routingSteps, stepInstance);
        assertNull(path);
    }

    @Test
    public void callEditRuleWithDifferentSections_returnsSummaryPage() {
        String expected = EditRule.FORM_SUMMARY;
        when(stepInstance.getEdit()).thenReturn(EditMode.SECTION);
        when(stepInstance.getStep()).thenReturn(createStep(Section.PERSONAL_DETAILS));
        when(routingSteps.getNextStep(anyString())).thenReturn(Optional.of(createStep(Section.NONE)));
        String path = sut.apply(routingSteps, stepInstance);
        assertThat(path, is(expected));
    }

    @Test
    public void callEditRuleWhenWeDontHaveANextStep_returnsNull() {
        when(stepInstance.getEdit()).thenReturn(EditMode.SECTION);
        when(stepInstance.getStep()).thenReturn(createStep(Section.PERSONAL_DETAILS));
        when(routingSteps.getNextStep(anyString())).thenReturn(Optional.empty());
        String path = sut.apply(routingSteps, stepInstance);
        assertNull(path);
    }

    @Test
    public void callEditRuleWithNone_returnsNull() {
        when(stepInstance.getEdit()).thenReturn(EditMode.NONE);
        String path = sut.apply(routingSteps, stepInstance);
        assertNull(path);
    }

    private Step createStep(final Section section) {
        return new Step("id", "", null, section);
    }

    private class FormFromTheSummary extends AbstractForm {
        @Override
        public Question getQuestion() {
            return null; // not needed here
        }

        @Override
        public void setQuestion(Question question) {
            // not needed here
        }

        public FormFromTheSummary(EditMode edit) {
            super.setEdit(edit);
        }
    }
}
