package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.validation.SortCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = SortCodeValidator.class)
public @interface SortCodeConstraint {
    String message() default "bankaccount.sortcode.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
