package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Abstract class for Date Range Questions. Class provides one validation constraint
 * (validates start date is before end date) and sub classes are expected to provide
 * any other validation (on their getters) that it may need for that particular date
 * range question, to easily give it independent error messaging
 *
 */

public abstract class DateRangeQuestion implements Question {

    protected DateQuestion startDate;
    protected DateQuestion endDate;

    public DateRangeQuestion(final DateQuestion startDate, final DateQuestion endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DateRangeQuestion() {
    }

    public abstract DateQuestion getStartDate();

    public abstract DateQuestion getEndDate();

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

