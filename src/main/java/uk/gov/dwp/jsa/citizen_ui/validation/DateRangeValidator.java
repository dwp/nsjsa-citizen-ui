package uk.gov.dwp.jsa.citizen_ui.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DateRangeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Service
public class DateRangeValidator implements ConstraintValidator<DateRangeConstraint, DateQuestion>, Validator {

    private LocalDate lowLimit;
    private LocalDate highLimit;

    private String lowLimitMessage;
    private String highLimitMessage;

    private boolean low;
    private boolean high;

    public void initialize(final DateRangeConstraint constraint) {
        this.high = constraint.high();
        this.low = constraint.low();

        this.highLimitMessage = constraint.highLimitMessage();
        this.lowLimitMessage = constraint.lowLimitMessage();

        if (this.low) {
            lowLimit = LocalDate.of(constraint.lowYear(), constraint.lowMonth(), constraint.lowDay());
        }

        if (this.high) {
            highLimit = LocalDate.of(constraint.highYear(), constraint.highMonth(), constraint.highDay());
        }

        if (this.low && this.high) {
            Assert.state(!highLimit.isBefore(lowLimit), "Low limit greater than High limit");
        }
    }

    public boolean isValid(final DateQuestion dateQuestion, final ConstraintValidatorContext context) {
        LocalDate date = LocalDate.of(dateQuestion.getYear(), dateQuestion.getMonth(), dateQuestion.getDay());

        boolean isValid = true;
        if (isDateInTheFuture(date)) {
            addInvalidMessage(context, highLimitMessage);
            return false;
        }
        if (!isLowValid(date, lowLimit, low)) {
            isValid = addInvalidMessage(context, lowLimitMessage);
        }
        if (!isHighValid(date, highLimit, high)) {
            isValid = addInvalidMessage(context, highLimitMessage);
        }
        return isValid;
    }

    public static boolean isLowValid(final LocalDate date, final LocalDate lowLimit, final boolean low) {
        return !low || !date.isBefore(lowLimit);
    }

    public static boolean isHighValid(final LocalDate date, final LocalDate highLimit, final boolean high) {
        return !high || !date.isAfter(highLimit);
    }

    private boolean isDateInTheFuture(final LocalDate startDate) {
        return startDate.isAfter(LocalDate.now());
    }

    public LocalDate getLowLimit() {
        return lowLimit;
    }

    public LocalDate getHighLimit() {
        return highLimit;
    }

    public String getLowLimitMessage() {
        return lowLimitMessage;
    }

    public String getHighLimitMessage() {
        return highLimitMessage;
    }

    public boolean isLow() {
        return low;
    }

    public boolean isHigh() {
        return high;
    }
}
