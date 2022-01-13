package uk.gov.dwp.jsa.citizen_ui.validation.constraints.common;

import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Constraint(validatedBy = DateValidator.class)
@Target({TYPE, FIELD, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateConstraint {

    String message() default "Invalid date";

    DateValidator.DateQuestionIdentify currentDateQuestion();

    String incompleteStartDateLocalePrefix() default "date.error.start";

    String incompleteEndDateLocalePrefix() default "date.error.end";

    String nonExistingDateLocalePrefix() default "date.error.unknown";

    String alphasDateLocalePrefix() default "date.error.alpha";

    String nonExistingSingleDateLocale() default  "date.error.single.unknown.date";

    String dayField() default "day";

    String monthField() default "month";

    String yearField() default "year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
