package uk.gov.dwp.jsa.citizen_ui.validation;

import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.JobEndDetailedReasonConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.regex.Pattern.matches;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion.ERROR_MESSAGE;
import static uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion.ERROR_MESSAGE_EMPTY;
import static uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndQuestion.ERROR_MESSAGE_INVALID;
import static uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails.WhyJobEndedReason.OTHER;

public class JobEndReasonValidator implements ConstraintValidator<JobEndDetailedReasonConstraint, WhyJobEndQuestion>,
        Validator {

    private static final String TOO_MANY_CHARACTERS = "previousemployment.employerdetails.too.many.char.error";
    private static final String WHY_JOB_ENDED_REGEX = Constants.STRING_FREE_FIELD_REGEX;
    private static final int REASON_MAX_LENGTH = 200;

    @Override
    public boolean isValid(
            final WhyJobEndQuestion whyJobEndQuestion,
            final ConstraintValidatorContext context) {

        String detailedReason = whyJobEndQuestion.getDetailedReason();
        WhyJobEndedReason whyJobEndedReason = whyJobEndQuestion.getWhyJobEndedReason();

        if (whyJobEndedReason == null) {
            return addInvalidMessage(context, ERROR_MESSAGE, "whyJobEndedReason");
        }

        if (!OTHER.equals(whyJobEndedReason)) {
            return true;
        }

        if (isBlank(detailedReason)) {
            return addInvalidMessage(context, ERROR_MESSAGE_EMPTY, "detailedReason");
        }

        if (!matches(WHY_JOB_ENDED_REGEX, detailedReason)) {
            return addInvalidMessage(context, ERROR_MESSAGE_INVALID, "detailedReason");
        }

        if (detailedReason.length() > REASON_MAX_LENGTH) {
            return addInvalidMessage(context, TOO_MANY_CHARACTERS, "detailedReason");
        }

        return true;
    }
}
