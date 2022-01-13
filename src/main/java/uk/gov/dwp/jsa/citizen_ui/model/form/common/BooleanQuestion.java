package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class BooleanQuestion implements Question {

    @NotNull
    private Boolean choice;

    public BooleanQuestion(@NotNull final Boolean choice) {
        this.choice = choice;
    }

    @SuppressWarnings("squid:S2637")
    public BooleanQuestion() {
        // Default constructor.
    }

    public Boolean getChoice() {
        return choice;
    }

    public void setChoice(final Boolean choice) {
        this.choice = choice;
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
