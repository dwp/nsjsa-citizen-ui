package uk.gov.dwp.jsa.citizen_ui.validation.constraints;


import uk.gov.dwp.jsa.citizen_ui.validation.OptionalUKPhoneNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ReportAsSingleViolation
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalUKPhoneNumberValidator.class)
public @interface OptionalUKPhoneNumberConstraint {
    String message() default "invalid.error";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
