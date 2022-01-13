package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.services.PhoneSanitiser;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.UKPhoneNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UKPhoneNumberValidator implements ConstraintValidator<UKPhoneNumberConstraint, PhoneQuestion>, Validator {

    private final PhoneSanitiser sanitiser;
    private static final int MIN_PHONE_LENGTH = 10;
    private static final int MAX_PHONE_LENGTH = 12;

    @Autowired
    public UKPhoneNumberValidator(final PhoneSanitiser sanitiser) {
        this.sanitiser = sanitiser;
    }

    private boolean validate(final String phone) {
        return phone.matches("0[0-9]+")
                && (phone.length() >= MIN_PHONE_LENGTH && phone.length() <= MAX_PHONE_LENGTH);
    }

    @Override
    public boolean isValid(final PhoneQuestion phoneQuestion, final ConstraintValidatorContext context) {
        if (phoneQuestion.getHasProvidedPhoneNumber() == null) {
            return addInvalidMessage(context, "contactpreferences.phone.mandatory", "hasProvidedPhoneNumber");
        } else if (phoneQuestion.getHasProvidedPhoneNumber()) {
            String phoneNumber = sanitiser.sanitise(phoneQuestion.getPhoneNumber());
            if (phoneNumber.startsWith("07") && phoneNumber.length() != 11) {
                return addInvalidMessage(context, "contactpreferences.phone.mobile.length", "phoneNumber");
            } else if (!validate(phoneNumber)) {
                return addInvalidMessage(context, "contactpreferences.phone.invalid.error", "phoneNumber");
            }
        }
        return true;
    }
}
