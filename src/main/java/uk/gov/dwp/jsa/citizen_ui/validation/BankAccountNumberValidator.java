package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BankAccountNumberConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.dwp.jsa.citizen_ui.model.form.BankAccountForm.ACCOUNT_NUMBER_REGEX;

@Component
public class BankAccountNumberValidator
        implements ConstraintValidator<BankAccountNumberConstraint, String>, Validator {

    private static final int MAX_LENGTH = 8;

    @Override
    public boolean isValid(final String accNum, final ConstraintValidatorContext context) {
        if (accNum == null || accNum.isEmpty()) {
            return addInvalidMessage(context, "bankaccount.accountnumber.blank");
        } else if (accNum.length() != MAX_LENGTH) {
            return addInvalidMessage(context, "bankaccount.accountnumber.length");
        } else if (!accNum.matches(ACCOUNT_NUMBER_REGEX)) {
            return addInvalidMessage(context, "bankaccount.accountnumber.pattern.error");
        }
        return true;
    }

}
