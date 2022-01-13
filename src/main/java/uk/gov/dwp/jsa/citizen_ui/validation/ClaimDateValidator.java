package uk.gov.dwp.jsa.citizen_ui.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.jsa.citizen_ui.model.form.ClaimStartDateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.ClaimDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;

import static java.time.LocalDate.now;
import static org.springframework.util.Assert.notNull;

public class ClaimDateValidator implements ConstraintValidator<ClaimDateConstraint, ClaimStartDateQuestion>, Validator {

    private static final int WEEKS_BEFORE_ALLOWED = 13;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimDateValidator.class);

    @Override
    public boolean isValid(
            final ClaimStartDateQuestion claimStartDateQuestion,
            final ConstraintValidatorContext context) {

        notNull(claimStartDateQuestion, "claimStartDateQuestion");

        Integer day = claimStartDateQuestion.getDay();
        Integer month = claimStartDateQuestion.getMonth();
        Integer year = claimStartDateQuestion.getYear();

        if (day != null && month != null && year != null) {
            LocalDate date;
            try {
                date = LocalDate.of(year, month, day);
            } catch (DateTimeException dte) {
                LOGGER.warn(dte.getMessage());
                //validdateconstraint will handle validation for date, ignore here
                //so we don't get multiple error messages for 1 error.
                return true;
            }

            LocalDate todayDate = now();
            LocalDate localDateBefore13Weeks = todayDate.minusWeeks(WEEKS_BEFORE_ALLOWED).minusDays(1);

            if (date.isAfter(todayDate)) {
                return addInvalidMessage(context, "claimstart.form.error.invalid.after");
            }

            if (date.isBefore(localDateBefore13Weeks)) {
                return addInvalidMessage(context,
                        "claimstart.form.error.invalid.before");
            }
        }

        return true;
    }
}
