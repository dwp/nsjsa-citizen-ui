package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.validation.CountConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CountConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CountConstraint {

    int min() default 1;

    String message() default Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
