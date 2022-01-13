package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.PostcodeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.POSTCODE_REGEX;

public class PostcodeValidator  implements ConstraintValidator<PostcodeConstraint, String>, Validator {


    @Override
    public boolean isValid(final String postcode, final ConstraintValidatorContext context) {
        if (postcode.isEmpty()) {
            return addInvalidMessage(context, "about.address.postcode.blank");
        } else {
            return validate(postcode);
        }
    }

    private boolean validate(final String postcode) {
        return postcode.matches(POSTCODE_REGEX);
    }
}
