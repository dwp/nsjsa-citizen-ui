package uk.gov.dwp.jsa.citizen_ui.validation.constraints;


import uk.gov.dwp.jsa.citizen_ui.validation.PersonalDetailsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PersonalDetailsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonalDetailsConstraint {

    String message() default "about.address.postcode.error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
