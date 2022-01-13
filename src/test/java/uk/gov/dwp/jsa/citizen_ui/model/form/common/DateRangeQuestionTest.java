package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.claimstart.JuryServiceDurationQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateRangeQuestionTest {

    private static final DateQuestion END_DATE_QUESTION = new DateQuestion();
    private static final DateQuestion START_DATE_QUESTION = new DateQuestion();
    private DateRangeQuestion question;

    @Test
    public void constructorSetsFields() {
        givenAQuestionWithParametersSet(START_DATE_QUESTION, END_DATE_QUESTION);
        thenTheStartDateIs(START_DATE_QUESTION);
        thenTheEndDateIs(END_DATE_QUESTION);
    }

    @Test
    public void setsStartDate() {
        givenAQuestion();
        whenISetStartDate(START_DATE_QUESTION);
        thenTheStartDateIs(START_DATE_QUESTION);
    }

    @Test
    public void setsEndDate() {
        givenAQuestion();
        whenISetEndDate(END_DATE_QUESTION);
        thenTheEndDateIs(END_DATE_QUESTION);
    }

    private void givenAQuestionWithParametersSet(final DateQuestion startDateQuestion, final DateQuestion endDateQuestion) {
        question = new JuryServiceDurationQuestion(startDateQuestion, endDateQuestion);
    }

    private void givenAQuestion() {
        question = new EducationDurationQuestion();
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
