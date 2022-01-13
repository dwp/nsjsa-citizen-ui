package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.Valid;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_PENSIONS_ALLOWED;

@CountConstraint()
public class ProvidersAddressForm extends AbstractCounterForm<ProvidersAddressQuestion> {

    @Valid
    private ProvidersAddressQuestion addressQuestion;

    public ProvidersAddressForm() {
        setMaxCount(MAX_PENSIONS_ALLOWED);
    }

    public ProvidersAddressForm(final ProvidersAddressQuestion addressQuestion) {
        this.addressQuestion = addressQuestion;
    }

    public ProvidersAddressQuestion getAddressQuestion() {
        if (addressQuestion == null) {
            addressQuestion = new ProvidersAddressQuestion();
        }
        return addressQuestion;
    }

    @Override
    public ProvidersAddressQuestion getQuestion() {
        return getAddressQuestion();
    }

    @Override
    public void setQuestion(final ProvidersAddressQuestion question) {
        setAddressQuestion(question);
    }

    public void setAddressQuestion(final ProvidersAddressQuestion addressQuestion) {
        this.addressQuestion = addressQuestion;
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
