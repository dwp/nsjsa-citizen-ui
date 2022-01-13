package uk.gov.dwp.jsa.citizen_ui.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.services.PhoneSanitiser;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.OptionalUKPhoneNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class OptionalUKPhoneNumberValidator implements ConstraintValidator<OptionalUKPhoneNumberConstraint, String> {
    private final PhoneSanitiser sanitiser;
    private static final int MIN_PHONE_LENGTH = 10;
    private static final int MAX_PHONE_LENGTH = 12;

    @Autowired
    public OptionalUKPhoneNumberValidator(final PhoneSanitiser sanitiser) {
        this.sanitiser = sanitiser;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {

        boolean empty = StringUtils.isBlank(value);

        if (empty) {
            return true;
        }

        return validate(sanitiser.sanitise(value));
    }

    private boolean validate(final String phone) {
        return phone.matches("0[0-9]+")
                && (phone.length() >= MIN_PHONE_LENGTH && phone.length() <= MAX_PHONE_LENGTH);
    }
}
