package uk.gov.dwp.jsa.citizen_ui.validation;

import org.thymeleaf.util.StringUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.address.OptionalPostcodeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressForm.POSTCODE_REGEX;

public class OptionalPostcodeValidator implements ConstraintValidator<OptionalPostcodeConstraint, String>, Validator {

    /**
     * This validates an optional postcode. An empty string or null are valid values. If neither is
     * the case, the postcode's pattern is validated.
     *
     * @param postcode to be validated
     * @param context validator context provided by Spring
     * @return true if a provided postcode is valid
     */
    @Override
    public boolean isValid(final String postcode, final ConstraintValidatorContext context) {
        if (!StringUtils.isEmpty(postcode) && !postcode.matches(POSTCODE_REGEX)) {
            return addInvalidMessage(context, "about.address.postcode.error");
        }
        return true;
    }
}
