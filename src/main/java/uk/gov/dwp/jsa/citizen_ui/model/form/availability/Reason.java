package uk.gov.dwp.jsa.citizen_ui.model.form.availability;


import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Q88 Why you can't attend the job centre interview.
 */
public class Reason {

    private boolean selected = false;

    public Reason() {
    }

    public Reason(final boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
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

    public uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Reason toDto() {
        return new uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.availability.Reason(this.selected);
    }
}
