package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.util.Strings;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class NameStringTruncatedQuestion extends StringQuestion {

    public static final int NAME_MAX_LENGTH = 27;
    public static final String NAME_VALIDATION_REGEX
            = "^(((\\p{IsLatin}+[. '\\-]?)+\\p{IsLatin}+)|(\\p{IsLatin}+))$";

    private boolean isValid;

    @NotNull
    @NotEmpty
    @Pattern(regexp = NAME_VALIDATION_REGEX)
    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public final void setValue(final String value) {
        super.setValue(sanitiseValue(value));
    }

    public final void setIsValid(final boolean isValid) {
        this.isValid = isValid;
    }

    public final boolean isValid() {
        return isValid;
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

    private String sanitiseValue(final String value) {
        String trimmedValue = value.trim();
        return Strings.truncate(trimmedValue, NAME_MAX_LENGTH);
    }
}
