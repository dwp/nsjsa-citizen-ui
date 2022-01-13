package uk.gov.dwp.jsa.citizen_ui.validation.constraints;

import uk.gov.dwp.jsa.citizen_ui.validation.BankAccountReferenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BankAccountReferenceValidator.class)
public @interface BankAccountReferenceConstraint {

    String message() default "bankaccount.accountnumber.pattern.error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
