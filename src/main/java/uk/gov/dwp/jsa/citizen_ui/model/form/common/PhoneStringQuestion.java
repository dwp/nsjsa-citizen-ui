package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @deprecated because this shall be consolidated into a single validation class.
 *
 * To be removed at some later date.
 */
@Deprecated
public class PhoneStringQuestion extends StringQuestion {

    public static final String PHONE_VALIDATION_REGEX = "^0([[0-9] ]?)+$";
    public static final int MIN_PHONE_SIZE = 10;
    public static final int MAX_PHONE_SIZE = 12;

    @NotEmpty(message = "invalid.error")
    @Size(min = MIN_PHONE_SIZE, max = MAX_PHONE_SIZE, message = "length.error",
            groups = ValidationSequence.BusinessValidationGroup.class)
    @Pattern(message = "invalid.error", regexp = PHONE_VALIDATION_REGEX,
            groups = ValidationSequence.BusinessValidationGroup.class)
    @Override
    public String getValue() {
        return super.getValue();
    }
}
