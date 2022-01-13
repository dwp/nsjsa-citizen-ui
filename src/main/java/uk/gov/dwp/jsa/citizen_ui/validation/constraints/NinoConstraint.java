package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.validation.NinoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NinoValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NinoConstraint {
    String message() default "error.invalid";
    String blankErrorMessage() default "error.blank";
    boolean isValid() default true;
    boolean isBlank() default  false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
