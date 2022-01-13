package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class UnableToWorkDueToIllnessQuestionTest {

    private UnableToWorkDueToIllnessQuestion booleanAndDateFieldQuestions;

    @Before
    public void setUp() throws Exception {
        this.booleanAndDateFieldQuestions = new UnableToWorkDueToIllnessQuestion();
    }

    @Test
    public void getHasProvidedAnswer() {
        ReflectionTestUtils.setField(booleanAndDateFieldQuestions, "hasProvidedAnswer", true);
        assertTrue(booleanAndDateFieldQuestions.getHasProvidedAnswer());
    }

    @Test
    public void getDateRangeQuestion() {
        DateQuestion start = new DateQuestion(1, 2, 2020);
        DateQuestion end = new DateQuestion(1, 3, 2020);
        DateRangeQuestionWithBoolean question = new DateRangeQuestionWithBoolean(start, end);
        ReflectionTestUtils.setField(booleanAndDateFieldQuestions, "dateRangeQuestion", question);
        DateRangeQuestionWithBoolean result = booleanAndDateFieldQuestions.getDateRangeQuestion();
        assertThat(result, is(question));
    }
}
