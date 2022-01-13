package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DynamicDateRangeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Service
public class DynamicDateRangeValidator
        implements ConstraintValidator<DynamicDateRangeConstraint, DateQuestion>, Validator {

    private DynamicDateRangeConstraint constraint;

    @Override
    public void initialize(final DynamicDateRangeConstraint constraint) {
        this.constraint = constraint;

        final LocalDate lowLimit = getLowLimit();
        final LocalDate highLimit = getHighLimit();

        if (constraint.low() && constraint.high()) {
            Assert.state(!highLimit.isBefore(lowLimit), "Low limit "
                    + "should be lower or equal than high limit");
        }
    }

    @Override
    public boolean isValid(final DateQuestion value, final ConstraintValidatorContext context) {

        LocalDate date;
        try {
            date = LocalDate.of(value.getYear(), value.getMonth(), value.getDay());
        } catch (NullPointerException e) {
            return false;
        }
        return isGreaterOrEqualsThanLowLimit(date, context)
                && isLessOrEqualThanHighLimit(date, context);
    }

    public LocalDate getLowLimit() {
        final LocalDate lowLimit = LocalDate.now().minusDays(constraint.daysAgo()).minusMonths(constraint.monthsAgo())
                .minusYears(constraint.yearsAgo());
        return lowLimit;
    }

    public LocalDate getHighLimit() {
        final LocalDate highLimit =
                LocalDate.now().plusDays(constraint.daysUntil()).plusMonths(constraint.monthsUntil())
                .plusYears(constraint.yearsUntil());
        return highLimit;
    }


    public String getLowLimitMessage() {
        return constraint.lowLimitMessage();
    }

    public String getHighLimitMessage() {
        return constraint.highLimitMessage();
    }

    public boolean isValidLow() {
        return constraint.low();
    }

    public boolean isValidHigh() {
        return constraint.high();
    }

    private boolean isGreaterOrEqualsThanLowLimit(final LocalDate date,
                                                  final ConstraintValidatorContext context) {
        final LocalDate lowLimit = getLowLimit();
        if (date.isBefore(lowLimit) && isValidLow()) {
            addInvalidMessage(context, getLowLimitMessage());
            return false;
        }
        return true;
    }

    private boolean isLessOrEqualThanHighLimit(final LocalDate date,
                                               final ConstraintValidatorContext context) {
        final LocalDate highLimit = getHighLimit();
        if (date.isAfter(highLimit) && isValidHigh()) {
            addInvalidMessage(context, getHighLimitMessage());
            return false;
        }
        return true;
    }
}
