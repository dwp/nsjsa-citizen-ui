package uk.gov.dwp.jsa.citizen_ui.validation;
/**
 * Validator class used to verify email address submission with boolean value.
 *
 * This component uses {@link uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser}
 * to remove whitespace from the email entered.
 */

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EmailConstraint;

import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class EmailValidator implements ConstraintValidator<EmailConstraint, EmailStringQuestion>, Validator {

    private final EmailSanitiser sanitiser;
    // Max email length.
    public static final int MAX_VALUE = 256;
    // E-mail regex.
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Autowired
    public EmailValidator(final EmailSanitiser sanitiser) {
        this.sanitiser = sanitiser;
    }

    @Override
    public boolean isValid(final EmailStringQuestion emailStringQuestion, final ConstraintValidatorContext context) {
        if (emailStringQuestion.getHasProvidedEmail() == null) {
            return addInvalidMessage(context, "contactpreferences.email.mandatory", "hasProvidedEmail");
        } else if (emailStringQuestion.getHasProvidedEmail()) {
            String email = sanitiser.sanitise(emailStringQuestion.getEmail());
            if (email.equals("")) {
                return addInvalidMessage(context, "contactpreferences.email.error.invalid", "email");
            } else if (email.length() > MAX_VALUE) {
                return addInvalidMessage(context, "contactpreferences.email.error.length", "email");
            } else if (!email.matches(EMAIL_REGEX)) {
                return addInvalidMessage(context, "contactpreferences.email.error.invalid", "email");
            }
        }
        return true;
    }
}
