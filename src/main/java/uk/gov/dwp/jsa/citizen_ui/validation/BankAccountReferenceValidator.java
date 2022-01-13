package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BankAccountReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm.REFERENCE_NUMBER_REGEX;

public class BankAccountReferenceValidator implements ConstraintValidator<BankAccountReferenceConstraint, String>,
                                                    Validator {
    private static final int MAX_LENGTH = 18;
    @Override
    public boolean isValid(final String bankAccReference, final ConstraintValidatorContext context) {
        if (bankAccReference != null) {
            if (bankAccReference.length() > MAX_LENGTH) {
                return addInvalidMessage(context, "bankaccount.reference.length");
            } else if (!bankAccReference.matches(REFERENCE_NUMBER_REGEX)) {
                return addInvalidMessage(context, "bankaccount.reference.pattern.invalid");
            }
        }
        return true;
    }
}
