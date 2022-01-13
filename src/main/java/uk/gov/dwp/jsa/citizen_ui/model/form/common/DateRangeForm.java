package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

@CountConstraint()
public class DateRangeForm extends AbstractCounterForm<DateRangeQuestion> {

    @Valid
    @NotNull
    private DateRangeQuestion dateRange;

    public DateRangeForm(final DateRangeQuestion dateRangeQuestion) {
        this.dateRange = dateRangeQuestion;
    }

    public DateRangeForm() {

    }

    public DateRangeQuestion getDateRange() {
        return dateRange;
    }

    @Override
    public DateRangeQuestion getQuestion() {
        return getDateRange();
    }

    @Override
    public void setQuestion(final DateRangeQuestion question) {
        setDateRange(question);
    }

    public void setDateRange(final DateRangeQuestion dateRange) {
        this.dateRange = dateRange;
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
