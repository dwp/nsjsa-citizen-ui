package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DateRangeQuestionWithBooleanTest {

    private DateRangeQuestionWithBoolean dateRangeQuestionWithBoolean;

    @Before
    public void setUp() {
        this.dateRangeQuestionWithBoolean = new DateRangeQuestionWithBoolean();
    }

    @Test
    public void getStartDate() {
        DateQuestion startDate = new DateQuestion(1, 1, LocalDate.now().getYear());
        ReflectionTestUtils.setField(dateRangeQuestionWithBoolean, "startDate", startDate);
        DateQuestion result = dateRangeQuestionWithBoolean.getStartDate();
        assertThat(result, is(startDate));

    }

    @Test
    public void getEndDate() {
        DateQuestion endDate = new DateQuestion(1, 2, LocalDate.now().getYear());
        ReflectionTestUtils.setField(dateRangeQuestionWithBoolean, "endDate", endDate);
        DateQuestion result = dateRangeQuestionWithBoolean.getEndDate();
        assertThat(result, is(endDate));
    }

    @Test
    public void setStartDate() {
        DateQuestion start = new DateQuestion(10, 11, LocalDate.now().getYear());
        dateRangeQuestionWithBoolean.setStartDate(start);
        assertThat(ReflectionTestUtils.getField(dateRangeQuestionWithBoolean, "startDate"), is(start));
    }

    @Test
    public void setEndDate() {
        DateQuestion end = new DateQuestion(10, 12, LocalDate.now().getYear());
        dateRangeQuestionWithBoolean.setEndDate(end);
        assertThat(ReflectionTestUtils.getField(dateRangeQuestionWithBoolean, "endDate"), is(end));
    }
}
