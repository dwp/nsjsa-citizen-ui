package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.validation.JobEndReasonValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion.ERROR_MESSAGE;

@Constraint(validatedBy = JobEndReasonValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JobEndDetailedReasonConstraint {
    String message() default ERROR_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
