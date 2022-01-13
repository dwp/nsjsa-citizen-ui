package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EnumConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Arrays.stream;
import static org.thymeleaf.util.StringUtils.isEmpty;

public class EnumValueValidator implements ConstraintValidator<EnumConstraint, String> {
    private EnumConstraint annotation;

    @Override
    public void initialize(final EnumConstraint annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        Class<? extends java.lang.Enum<?>> enumClass = this.annotation.enumClass();
        if (isEmpty(value)) {
            return false;
        }
        return stream(enumClass.getEnumConstants())
                .map(java.lang.Enum::toString)
                .anyMatch(value::equals);
    }

    public EnumConstraint getAnnotation() {
        return annotation;
    }
}
