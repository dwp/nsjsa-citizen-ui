package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmployersAddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Q43 Keeps previous employer's details.
 */
public class EmployersDetails {

    /**
     * Q41a Previous employer's start date.
     */
    private DateQuestion startDate;

    /**
     * Q41b Previous employer's end date.
     */
    private DateQuestion endDate;
    /**
     * Q42 Why Job Ended Question.
     */
    private WhyJobEndQuestion whyJobEndQuestion;
    /**
     * Q43 Previous employer's name question.
     */
    private StringQuestion employersNameQuestion;

    /**
     * Q45 Previous employer's address question.
     */
    private EmployersAddressQuestion employersAddressQuestion;

    /**
     * Q46 Previous employer's phone question.
     */
    private StringQuestion employersPhoneQuestion;
    /**
     * Q49 Expecting payment from last 6 months question response.
     */
    private BooleanQuestion expectPaymentQuestion;
    /**
     * Q51 Employment status question response.
     */
    private BooleanQuestion employmentStatusQuestion;

    public EmployersDetails(final StringQuestion employersNameQuestion,
                            final DateQuestion startDate,
                            final DateQuestion endDate,
                            final EmployersAddressQuestion employersAddressQuestion,
                            final BooleanQuestion employmentStatusQuestion,
                            final BooleanQuestion expectPaymentQuestion) {
        this.employersNameQuestion = employersNameQuestion;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employersAddressQuestion = employersAddressQuestion;
        this.employmentStatusQuestion = employmentStatusQuestion;
        this.expectPaymentQuestion = expectPaymentQuestion;
    }

    public EmployersDetails() {
    }

    public StringQuestion getEmployersNameQuestion() {
        if (employersNameQuestion == null) {
            employersNameQuestion = new StringQuestion();
        }
        return employersNameQuestion;
    }

    public void setEmployersNameQuestion(final StringQuestion employersNameQuestion) {
        this.employersNameQuestion = employersNameQuestion;
    }

    public DateQuestion getStartDate() {
        if (startDate == null) {
            startDate = new DateQuestion();
        }
        return startDate;
    }

    public void setStartDate(final DateQuestion startDate) {
        this.startDate = startDate;
    }

    public DateQuestion getEndDate() {
        if (endDate == null) {
            endDate = new DateQuestion();
        }
        return endDate;
    }

    public void setEndDate(final DateQuestion endDate) {
        this.endDate = endDate;
    }

    public StringQuestion getEmployersPhoneQuestion() {
        if (employersPhoneQuestion == null) {
            employersPhoneQuestion = new StringQuestion();
        }
        return employersPhoneQuestion;
    }

    public void setEmployersPhoneQuestion(final StringQuestion employersPhoneQuestion) {
        this.employersPhoneQuestion = employersPhoneQuestion;
    }

    public WhyJobEndQuestion getWhyJobEndQuestion() {
        return whyJobEndQuestion;
    }

    public void setWhyJobEndQuestion(final WhyJobEndQuestion whyJobEndQuestion) {
        this.whyJobEndQuestion = whyJobEndQuestion;
    }

    public EmployersAddressQuestion getEmployersAddressQuestion() {
        if (employersAddressQuestion == null) {
            employersAddressQuestion = new EmployersAddressQuestion();
        }
        return employersAddressQuestion;
    }

    public void setEmployersAddressQuestion(final EmployersAddressQuestion employersAddressQuestion) {
        this.employersAddressQuestion = employersAddressQuestion;
    }

    public BooleanQuestion getEmploymentStatusQuestion() {
        return employmentStatusQuestion;
    }

    public void setEmploymentStatusQuestion(final BooleanQuestion employmentStatusQuestion) {
        this.employmentStatusQuestion = employmentStatusQuestion;
    }

    public BooleanQuestion getExpectPaymentQuestion() {
        return expectPaymentQuestion;
    }

    public void setExpectPaymentQuestion(final BooleanQuestion expectPaymentQuestion) {
        this.expectPaymentQuestion = expectPaymentQuestion;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
