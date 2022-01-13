package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class EmployersDetailsTest {

    public static final StringQuestion EMPLOYERS_NAME_QUESTION = new StringQuestion();
    public static final DateQuestion START_DATE = new DateQuestion();
    public static final DateQuestion END_DATE = new DateQuestion();
    public static final StringQuestion EMPLOYERS_PHONE_QUESTION = new StringQuestion();
    public static final WhyJobEndQuestion WHY_JOB_END_QUESTION = new WhyJobEndQuestion();
    public static final EmployersAddressQuestion EMPLOYERS_ADDRESS_QUESTION = new EmployersAddressQuestion();
    public static final BooleanQuestion EMPLOYMENT_STATUS_QUESTION = new BooleanQuestion();
    public static final BooleanQuestion EXPECT_PAYMENT_QUESTION = new BooleanQuestion();

    @Test
    public void constructorSetsFieldValues() {
        final EmployersDetails employersDetails = new EmployersDetails(
                EMPLOYERS_NAME_QUESTION, START_DATE, END_DATE, EMPLOYERS_ADDRESS_QUESTION,
                EMPLOYMENT_STATUS_QUESTION, EXPECT_PAYMENT_QUESTION);
        assertThat(employersDetails.getEmployersNameQuestion(), is(EMPLOYERS_NAME_QUESTION));
        assertThat(employersDetails.getStartDate(), is(START_DATE));
        assertThat(employersDetails.getEndDate(), is(END_DATE));
        assertThat(employersDetails.getEmployersPhoneQuestion(), is(EMPLOYERS_PHONE_QUESTION));
        assertThat(employersDetails.getWhyJobEndQuestion(), is(nullValue()));
        assertThat(employersDetails.getEmployersAddressQuestion(), is(EMPLOYERS_ADDRESS_QUESTION));
        assertThat(employersDetails.getEmploymentStatusQuestion(), is(EMPLOYMENT_STATUS_QUESTION));
        assertThat(employersDetails.getExpectPaymentQuestion(), is(EXPECT_PAYMENT_QUESTION));
    }

    @Test
    public void hasDefaultValues() {
        final EmployersDetails employersDetails = new EmployersDetails();
        assertThat(employersDetails.getEmployersNameQuestion(), is(EMPLOYERS_NAME_QUESTION));
        assertThat(employersDetails.getStartDate(), is(START_DATE));
        assertThat(employersDetails.getEndDate(), is(END_DATE));
        assertThat(employersDetails.getEmployersPhoneQuestion(), is(EMPLOYERS_PHONE_QUESTION));
        assertThat(employersDetails.getWhyJobEndQuestion(), is(nullValue()));
        assertThat(employersDetails.getEmployersAddressQuestion(), is(EMPLOYERS_ADDRESS_QUESTION));
        assertThat(employersDetails.getEmploymentStatusQuestion(), is(nullValue()));
        assertThat(employersDetails.getExpectPaymentQuestion(), is(nullValue()));
    }

    @Test
    public void setEmployersNameQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEmployersNameQuestion(EMPLOYERS_NAME_QUESTION);
        assertThat(employersDetails.getEmployersNameQuestion(), is(EMPLOYERS_NAME_QUESTION));
    }

    @Test
    public void setStartDate() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setStartDate(START_DATE);
        assertThat(employersDetails.getStartDate(), is(START_DATE));
    }

    @Test
    public void setEndDate() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEndDate(END_DATE);
        assertThat(employersDetails.getEndDate(), is(END_DATE));
    }

    @Test
    public void setEmployersPhoneQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEmployersPhoneQuestion(EMPLOYERS_PHONE_QUESTION);
        assertThat(employersDetails.getEmployersPhoneQuestion(), is(EMPLOYERS_PHONE_QUESTION));
    }

    @Test
    public void setWhyJobEndQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setWhyJobEndQuestion(WHY_JOB_END_QUESTION);
        assertThat(employersDetails.getWhyJobEndQuestion(), is(WHY_JOB_END_QUESTION));
    }

    @Test
    public void setEmployersAddressQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEmployersAddressQuestion(EMPLOYERS_ADDRESS_QUESTION);
        assertThat(employersDetails.getEmployersAddressQuestion(), is(EMPLOYERS_ADDRESS_QUESTION));
    }

    @Test
    public void setEmploymentStatusQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setEmploymentStatusQuestion(EMPLOYMENT_STATUS_QUESTION);
        assertThat(employersDetails.getEmploymentStatusQuestion(), is(EMPLOYMENT_STATUS_QUESTION));
    }

    @Test
    public void setExpectPaymentQuestion() {
        final EmployersDetails employersDetails = new EmployersDetails();
        employersDetails.setExpectPaymentQuestion(EXPECT_PAYMENT_QUESTION);
        assertThat(employersDetails.getExpectPaymentQuestion(), is(EXPECT_PAYMENT_QUESTION));
    }
}
