package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.util.StringUtils;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EmptyStringConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyStringConstraintValidator implements ConstraintValidator<EmptyStringConstraint, String> {

    public boolean isValid(final String honeyPotField, final ConstraintValidatorContext context) {
        return StringUtils.isEmpty(honeyPotField);
    }
}
