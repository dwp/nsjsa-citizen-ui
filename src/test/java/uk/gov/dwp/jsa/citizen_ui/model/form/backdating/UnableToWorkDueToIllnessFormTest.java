package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class UnableToWorkDueToIllnessFormTest {

    private UnableToWorkDueToIllnessForm unableToWorkDueToIllnessForm;

    @Before
    public void setUp() {
        this.unableToWorkDueToIllnessForm = new UnableToWorkDueToIllnessForm();
    }

    @Test
    public void getQuestion() {
        DateQuestion start = new DateQuestion(1, 2, 2020);
        DateQuestion end = new DateQuestion(1, 3, 2020);
        DateRangeQuestionWithBoolean dateRangeQuestion = new DateRangeQuestionWithBoolean(start, end);
        BooleanAndDateFieldQuestions questions = new UnableToWorkDueToIllnessQuestion(true, dateRangeQuestion);

        ReflectionTestUtils.setField(unableToWorkDueToIllnessForm, "unableToWorkDueToIllnessQuestion", questions);

        BooleanAndDateFieldQuestions result = unableToWorkDueToIllnessForm.getQuestion();
        assertThat(result, is(questions));
    }

    @Test
    public void setQuestion() {
        DateQuestion start = new DateQuestion(1, 2, 2020);
        DateQuestion end = new DateQuestion(1, 3, 2020);
        DateRangeQuestionWithBoolean dateRangeQuestion = new DateRangeQuestionWithBoolean(start, end);
        UnableToWorkDueToIllnessQuestion questions = new UnableToWorkDueToIllnessQuestion(true, dateRangeQuestion);

        unableToWorkDueToIllnessForm.setQuestion(questions);

        assertThat(ReflectionTestUtils.getField(unableToWorkDueToIllnessForm, "unableToWorkDueToIllnessQuestion"), is(questions));
    }

    @Test
    public void radioOptions() {
        assertThat(unableToWorkDueToIllnessForm.radioOptions(), is(Arrays.asList(true, false)));
    }

    @Test
    public void hasNoGuard() {
        assertTrue(unableToWorkDueToIllnessForm.hasNoGuard());
    }
}
