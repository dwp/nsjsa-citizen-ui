package uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DateRangeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DynamicDateRangeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.PeriodConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

@PeriodConstraint(message = "previousemployment.dates.error.startdate.after.enddate",
        groups = ValidationSequence.BusinessValidationGroup.class)
public class EmploymentDurationQuestion extends DateRangeQuestion {

    private static final int END_DATE_THRESHOLD_SIX_MONTHS = 6;
    private static final int MIN_PREVIOUS_EMPLOYMENT_START_YEAR = 1950;

    public EmploymentDurationQuestion(final DateQuestion startDate, final DateQuestion endDate) {
        super(startDate, endDate);
    }

    public EmploymentDurationQuestion() {
    }

    @Override
    @ValidDateConstraint(message = "previousemployment.dates.error.empty.startdate",
                        alphasDateLocalePrefix = "previousemployment.dates.error.alpha",
                        nonExistingDateLocalePrefix = "previousemployment.dates.error.real",
                        currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_START_DATE)
    @DateRangeConstraint(
            lowLimitMessage = "previousemployment.dates.error.min.startdate",
            highLimitMessage = "previousemployment.date.error.max.startdate",
            low = true,
            lowDay = 1,
            lowMonth = 1,
            lowYear = MIN_PREVIOUS_EMPLOYMENT_START_YEAR,
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getStartDate() {
        return startDate;
    }

    @Override
    @ValidDateConstraint(message = "previousemployment.dates.error.empty.enddate",
                        alphasDateLocalePrefix = "previousemployment.dates.error.alpha",
                        nonExistingDateLocalePrefix = "previousemployment.dates.error.real",
                        currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_END_DATE)
    @DynamicDateRangeConstraint(message = "previousemployment.dates.error.end.date.exceeds.threshold",
            lowLimitMessage = "previousemployment.dates.error.end.date.exceeds.threshold",
            highLimitMessage = "previousemployment.date.error.max.enddate",
            monthsAgo = END_DATE_THRESHOLD_SIX_MONTHS,
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getEndDate() {
        return endDate;
    }
}
