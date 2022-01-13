package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import java.time.LocalDate;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static uk.gov.dwp.jsa.citizen_ui.Constants.SDT;

/**
 * Captures a date question.
 */
public class DateQuestion implements Question {

    /**
     * The day of the month the jury service started.
     */
    private Integer day;
    /**
     * The month the jury service started.
     */
    private Integer month;

    /**
     * The year the jury service started.
     */
    private Integer year;


    @SuppressWarnings("squid:S2637")
    public DateQuestion() {
        //Default Constructor
    }

    public DateQuestion(final Integer day, final Integer month, final Integer year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public DateQuestion(final LocalDate localDate) {
        Assert.notNull(localDate, "Date should not be null");
        this.day = localDate.getDayOfMonth();
        this.month = localDate.getMonthValue();
        this.year = localDate.getYear();
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(final Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(final Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    public String getFormattedDate() {
        return LocalDate.of(year, month, day).format(SDT);
    }

    public LocalDate getLocalDate() {
        return LocalDate.of(year, month, day);
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
