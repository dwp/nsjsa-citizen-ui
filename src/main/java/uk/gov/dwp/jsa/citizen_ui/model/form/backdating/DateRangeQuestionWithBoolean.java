package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class DateRangeQuestionWithBoolean {

    private DateQuestion startDate;
    private DateQuestion endDate;

    public DateRangeQuestionWithBoolean(final DateQuestion startDate, final DateQuestion endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DateRangeQuestionWithBoolean() {
    }

    public DateQuestion getStartDate() {
        return this.startDate;
    }

    public DateQuestion getEndDate() {
        return this.endDate;
    }

    public void setStartDate(final DateQuestion startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(final DateQuestion endDate) {
        this.endDate = endDate;
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
