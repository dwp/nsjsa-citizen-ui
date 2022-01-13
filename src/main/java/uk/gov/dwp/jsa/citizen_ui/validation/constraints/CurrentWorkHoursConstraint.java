package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@NotNull
@Positive
@ReportAsSingleViolation
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface CurrentWorkHoursConstraint {
    String message() default "currentwork.hours.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
