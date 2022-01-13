package uk.gov.dwp.jsa.citizen_ui.model.form.availability;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.AvailabilitySelectionConstraint;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Q88 Why you can't attend the job centre interview.
 */
public class AttendInterviewQuestion implements Question {

    @Valid
    @AvailabilitySelectionConstraint
    private List<Day> daysNotToAttend;

    public AttendInterviewQuestion() {
    }
    public AttendInterviewQuestion(final List<Day> daysNotToAttend) {
        this.daysNotToAttend = new ArrayList<>(daysNotToAttend);
    }

    @SuppressWarnings("squid:S2384") // Supressed because spring is unable bind to each of the items in the collection
    public List<Day> getDaysNotToAttend() {
        return daysNotToAttend;
    }

    public void setDaysNotToAttend(final List<Day> daysNotToAttend) {
        this.daysNotToAttend = new ArrayList<>(daysNotToAttend);
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
