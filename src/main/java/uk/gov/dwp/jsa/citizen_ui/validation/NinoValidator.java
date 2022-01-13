package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.services.NinoSanitiser;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.NinoConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NinoValidator implements ConstraintValidator<NinoConstraint, String>, Validator {
    private static final String NINO_PATTERN =
            "^(?!BG|GB|NK|KN|TN|NT|ZZ)[A-CEGHJ-PR-TW-Z][A-CEGHJ-NPR-TW-Z](?:\\s*\\d{2}){3}\\s*[A-D]$";

    private final NinoSanitiser ninoSanitiser;

    private String isBlankMessage = "nino.form.error.blank";

    private boolean isValid;
    private boolean isBlank;

    @Autowired
    public NinoValidator(final NinoSanitiser ninoSanitiser) {
        this.ninoSanitiser = ninoSanitiser;
    }

    public void initialize(final NinoConstraint ninoConstraint) {
        this.isBlankMessage = ninoConstraint.blankErrorMessage();
        this.isBlank = ninoConstraint.isBlank();
        this.isValid = ninoConstraint.isValid();
    }
    @Override
    public final boolean isValid(final String value,
                           final ConstraintValidatorContext context) {
        isBlank = value.isEmpty();
        isValid = validate(ninoSanitiser.sanitise(value));
        if (isBlank) {
            return addInvalidMessage(context, isBlankMessage);
        } else {
            return isValid;
        }
    }

    private boolean validate(final String nino) {
        return nino.matches(NINO_PATTERN);
    }
}
