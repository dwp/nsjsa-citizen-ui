package uk.gov.dwp.jsa.citizen_ui.validation;

import javax.validation.ConstraintValidatorContext;

public interface Validator {

    default boolean addInvalidMessage(final ConstraintValidatorContext context, final String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }

    default boolean addInvalidMessage(final ConstraintValidatorContext context, final String message,
                                      final String propertyNode) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(propertyNode).addConstraintViolation();
        return false;
    }
}
