package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class WhyNotApplySoonerQuestion extends StringQuestion {

    private static final int STRING_FIELD_MAX_SIZE = 600;

    @Size(max = STRING_FIELD_MAX_SIZE, message = "backdating.whynow.details.error.max.chars")
    @NotBlank(message = "backdating.whynow.details.error.empty")
    @Pattern(regexp = Constants.STRING_FREE_FIELD_REGEX, message = "backdating.whynow.details.error.invalid")
    @Override
    public String getValue() {
        return super.getValue();
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
