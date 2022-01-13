package uk.gov.dwp.jsa.citizen_ui.validation.constraints.common;

import uk.gov.dwp.jsa.citizen_ui.validation.DateRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;

/**
 * Validate if a date is in certain limits.
 * Limits are inclusive.
 * You can use negative values.
 */
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRangeConstraint {

    String message() default COMMON_FORM_ERROR_SUMMARY_TITLE;

    String lowLimitMessage() default COMMON_FORM_ERROR_SUMMARY_TITLE;

    String highLimitMessage() default COMMON_FORM_ERROR_SUMMARY_TITLE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean low() default false;

    boolean high() default false;

    int lowDay() default 0;

    int lowMonth() default 0;

    int lowYear() default 0;

    int highDay() default 0;

    int highMonth() default 0;

    int highYear() default 0;
}
