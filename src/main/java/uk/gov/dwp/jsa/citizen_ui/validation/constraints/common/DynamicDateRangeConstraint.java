package uk.gov.dwp.jsa.citizen_ui.validation.constraints.common;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.validation.DynamicDateRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validate if a date is in certain limits.
 * Limits are inclusive.
 * You can use negative values.
 */
@Constraint(validatedBy = DynamicDateRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicDateRangeConstraint {

    String message() default Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;

    String lowLimitMessage() default Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;

    String highLimitMessage() default Constants.COMMON_FORM_ERROR_SUMMARY_TITLE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean low() default true;

    boolean high() default true;

    int daysAgo() default 0;

    int monthsAgo() default 0;

    int yearsAgo() default 0;

    int daysUntil() default 0;

    int monthsUntil() default 0;

    int yearsUntil() default 0;
}
