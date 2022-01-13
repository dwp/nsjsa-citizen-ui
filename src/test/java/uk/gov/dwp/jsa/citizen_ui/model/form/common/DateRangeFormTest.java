package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateRangeFormTest {

    private static final DateRangeQuestion DATE_RANGE_EDUCATION_DURATION = new EducationDurationQuestion();

    private DateRangeForm form;

    @Test
    public void setsDateRange() {
        givenAForm();
        whenISetDateRange(DATE_RANGE_EDUCATION_DURATION);
        thenTheDateRangeIs(DATE_RANGE_EDUCATION_DURATION);
    }

    @Test
    public void setsQuestion() {
        givenAForm();
        whenISetQuestion(DATE_RANGE_EDUCATION_DURATION);
        thenTheQuestionIs(DATE_RANGE_EDUCATION_DURATION);
    }

    private void givenAForm() {
        form  = new DateRangeForm();
    }

    private void whenISetDateRange(final DateRangeQuestion dateRange) {
        form.setDateRange(dateRange);
    }

    private void whenISetQuestion(final DateRangeQuestion dateRange) {
        form.setQuestion(dateRange);
    }

    private void thenTheQuestionIs(final DateRangeQuestion dateRange) {
        assertThat(form.getQuestion(), is(dateRange));
    }

    private void thenTheDateRangeIs(final DateRangeQuestion dateRange) {
        assertThat(form.getDateRange(), is(dateRange));
    }



}
