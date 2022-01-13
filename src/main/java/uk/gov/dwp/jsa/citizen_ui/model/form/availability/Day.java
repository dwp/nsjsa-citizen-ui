package uk.gov.dwp.jsa.citizen_ui.model.form.availability;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Q88 Why you can't attend the job centre interview.
 */
public class Day {

    private LocalDate date;

    @Valid
    private Reason morning;

    @Valid
    private Reason afternoon;

    public Day() {
    }

    public Day(final LocalDate date, final Reason morning, final Reason afternoon) {
        this.date = date;
        this.morning = morning;
        this.afternoon = afternoon;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public Reason getMorning() {
        return morning;
    }

    public void setMorning(final Reason morning) {
        this.morning = morning;
    }

    public Reason getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(final Reason afternoon) {
        this.afternoon = afternoon;
    }

    public static boolean isWeekendDay(final LocalDate date) {
        return (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
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
