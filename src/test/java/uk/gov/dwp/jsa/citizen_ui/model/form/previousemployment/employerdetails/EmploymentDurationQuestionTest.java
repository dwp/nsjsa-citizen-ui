package uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmploymentDurationQuestionTest {
    private static final DateQuestion END_DATE_QUESTION = new DateQuestion();
    private static final DateQuestion START_DATE_QUESTION = new DateQuestion();
    private EmploymentDurationQuestion question;

    @Test
    public void getsStartDate() {
        givenAQuestion();
        whenISetStartDate(START_DATE_QUESTION);
        thenTheStartDateIs(START_DATE_QUESTION);
    }

    @Test
    public void getsEndDate() {
        givenAQuestion();
        whenISetEndDate(END_DATE_QUESTION);
        thenTheEndDateIs(END_DATE_QUESTION);
    }

    private void givenAQuestion() {
        question = new EmploymentDurationQuestion();
    }

    private void whenISetStartDate(final DateQuestion dateQuestion) {
        question.setStartDate(dateQuestion);
    }

    private void whenISetEndDate(final DateQuestion dateQuestion) {
        question.setEndDate(dateQuestion);
    }

    private void thenTheStartDateIs(final DateQuestion expectedDateQuestion) {
        assertThat(question.getStartDate(), is(expectedDateQuestion));
    }

    private void thenTheEndDateIs(final DateQuestion expectedDateQuestion) {
        assertThat(question.getEndDate(), is(expectedDateQuestion));
    }
}
