package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.Valid;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;

@CountConstraint()
public class EmployersAddressForm extends AbstractCounterForm<EmployersAddressQuestion> {
    /**
     * Q45 Employer's address response.
     */
    @Valid
    private EmployersAddressQuestion employersAddressQuestion;

    public EmployersAddressForm() {
        setMaxCount(MAX_JOBS_ALLOWED);
    }

    public EmployersAddressForm(final EmployersAddressQuestion employersAddressQuestion) {
        this.employersAddressQuestion = employersAddressQuestion;
    }

    public EmployersAddressQuestion getEmployersAddressQuestion() {
        if (employersAddressQuestion == null) {
            employersAddressQuestion = new EmployersAddressQuestion();
        }
        return employersAddressQuestion;
    }

    @Override
    public EmployersAddressQuestion getQuestion() {
        return getEmployersAddressQuestion();
    }

    @Override
    public void setQuestion(final EmployersAddressQuestion question) {
        setEmployersAddressQuestion(question);
    }

    public void setEmployersAddressQuestion(final EmployersAddressQuestion employersAddressQuestion) {
        this.employersAddressQuestion = employersAddressQuestion;
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
