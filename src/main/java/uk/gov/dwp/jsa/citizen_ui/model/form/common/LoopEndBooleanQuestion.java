package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class LoopEndBooleanQuestion extends BooleanQuestion {
    private boolean hasMoreThanLimit;

    public LoopEndBooleanQuestion(final Boolean choice,
                                  final boolean hasMoreThanLimit) {
        super(choice);
        this.hasMoreThanLimit = hasMoreThanLimit;
    }

    public LoopEndBooleanQuestion() {
        // Default Constructor
    }

    public LoopEndBooleanQuestion(final Boolean choice) {
        this.setChoice(choice);
    }

    public boolean getHasMoreThanLimit() {
        return hasMoreThanLimit;
    }

    public void setHasMoreThanLimit(final boolean hasMoreThanLimit) {
        this.hasMoreThanLimit = hasMoreThanLimit;
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
