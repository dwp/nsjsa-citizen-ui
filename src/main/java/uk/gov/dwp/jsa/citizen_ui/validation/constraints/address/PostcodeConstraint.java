package uk.gov.dwp.jsa.citizen_ui.validation.constraints.address;

import uk.gov.dwp.jsa.citizen_ui.validation.PostcodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ReportAsSingleViolation
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PostcodeValidator.class)
public @interface PostcodeConstraint {

    String message() default "about.address.postcode.error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
