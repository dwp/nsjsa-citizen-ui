package uk.gov.dwp.jsa.citizen_ui.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.form.backdating.AskedForAdviceQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.HasHadAdviceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class HasHadAdviceValidator implements ConstraintValidator<HasHadAdviceConstraint, AskedForAdviceQuestion>, Validator {

    private static final int STRING_FIELD_MAX_SIZE = 600;

    @Override
    public boolean isValid(final AskedForAdviceQuestion question, final ConstraintValidatorContext context) {
        if (question.getHasHadAdvice() == null) {
            return addInvalidMessage(context, "backdating.asked.for.advice.error.mandatory", "hasHadAdvice");
        } else if (question.getHasHadAdvice()) {
            String value = question.getValue();
            if (StringUtils.isBlank(value)) {
                return addInvalidMessage(context, "backdating.asked.for.advice.error.empty", "value");
            }
            if (!value.matches(Constants.STRING_FREE_FIELD_REGEX)) {
                return addInvalidMessage(context, "backdating.asked.for.advice.error.invalid", "value");
            }
            if (value.length() > STRING_FIELD_MAX_SIZE) {
                return addInvalidMessage(context, "backdating.asked.for.advice.error.max.chars", "value");
            }
        }
        return true;
    }


}
