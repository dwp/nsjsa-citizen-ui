package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.validation.BooleanWithHiddenDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BooleanWithHiddenDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanWithHiddenDateConstraint {
    String message() default "Error";
    String missingYesNoAnswerLocal();
    String missingStartDateLocal();
    String missingEndDateLocal();
    String startDateMustBeAfterMinimumAllowedDateLocal();
    String endDateMustBeAfterMinimumAllowedDateLocal();
    String startDateIsAfterEndDateLocal();
    String startDateMustBeInThePastLocal();
    String endDateMustBeInThePastLocal();
    BooleanWithHiddenDateValidator.QuestionIdentifier questionIdentifier();
    String startDateMustBeReal();
    String dateCantBeAlpha();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
