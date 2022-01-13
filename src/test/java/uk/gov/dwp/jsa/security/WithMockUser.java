package uk.gov.dwp.jsa.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import uk.gov.dwp.jsa.security.roles.Role;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(
        factory = WithMockCustomUserSecurityContextFactory.class
)
public @interface WithMockUser {
    String value() default "First Last";

    String staffNumber() default "1234567890";

    String token() default "";

    Role role() default Role.CITIZEN;

}
