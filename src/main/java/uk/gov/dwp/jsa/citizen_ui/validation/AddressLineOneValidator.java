package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.AddressLineOneConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX;
import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.TOWN_OR_CITY_LINE_REGEX;

public class AddressLineOneValidator implements ConstraintValidator<AddressLineOneConstraint, String>, Validator {

    @Override
    public boolean isValid(final String addressLineOne, final ConstraintValidatorContext context) {
        if (addressLineOne == null || addressLineOne.isEmpty()) {
            return addInvalidMessage(context, "about.address.line1.blank");
        } else if (!addressLineOne.matches(TOWN_OR_CITY_LINE_REGEX)) {
            return addInvalidMessage(context, "about.address.line1.error");
        } else if (!addressLineOne.matches(NOT_MORE_THAN_TWO_CONSECUTIVE_SPECIAL_CHARS_REGEX)) {
            return addInvalidMessage(context, "about.address.line1.error");
        }
        return true;
    }
}
