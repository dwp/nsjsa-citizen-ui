package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EducationCourseHoursConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import static uk.gov.dwp.jsa.citizen_ui.Constants.STRING_DECIMAL_NUMBER_REGEX;

public class BigDecimalPatternValidator implements ConstraintValidator<EducationCourseHoursConstraint, BigDecimal>, Validator {

    @Override
    public boolean isValid(final BigDecimal courseHours, final ConstraintValidatorContext context) {
        try {
            if (null == courseHours || (courseHours.compareTo(new BigDecimal("0.0")) == 0)) {
                return addInvalidMessage(context, "education.coursehours.empty");
            } else {
                    String strCourseHours = courseHours.toString();
                    if (!strCourseHours.matches(STRING_DECIMAL_NUMBER_REGEX)) {
                        return addInvalidMessage(context, "education.coursehours.invalid");
                    }
                }
            } catch (Exception e) {
            return addInvalidMessage(context, "education.coursehours.invalid");
        }
        return true;
    }
}
