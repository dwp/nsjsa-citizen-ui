package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DynamicDateRangeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.PeriodConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

@PeriodConstraint(message = "education.courseduration.error.startdate.after.enddate",
        groups = ValidationSequence.BusinessValidationGroup.class)
public class EducationDurationQuestion extends DateRangeQuestion {
    protected static final int START_DATE_MIN_YEARS_AGO       = 10;
    protected static final int END_DATE_MIN_YEARS_AGO       = 4;
    protected static final int END_DATE_MAX_YEARS_UNTIL       = 10;
    protected static final int START_DATE_MAX_UNTIL_YESTERDAY = -1;

    public EducationDurationQuestion(final DateQuestion startDate, final DateQuestion endDate) {
        super(startDate, endDate);
    }

    public EducationDurationQuestion() {

    }

    /**
     * Q24a EducationDetails start date.
     */
    @Override
    @ValidDateConstraint(message = "education.courseduration.empty.start",
            incompleteStartDateLocalePrefix = "education.courseduration.error.start",
            nonExistingDateLocalePrefix = "education.courseduration.real",
            alphasDateLocalePrefix = "education.courseduration.alpha",
            currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_START_DATE)
    @DynamicDateRangeConstraint(yearsAgo = START_DATE_MIN_YEARS_AGO, daysUntil = START_DATE_MAX_UNTIL_YESTERDAY,
            lowLimitMessage = "education.courseduration.error.min.startdate",
            highLimitMessage = "education.courseduration.error.max.startdate",
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getStartDate() {
        return this.startDate;
    }

    /**
     * Q24b EducationDetails end date.
     */
    @Override
    @ValidDateConstraint(message = "education.courseduration.empty.end",
            incompleteEndDateLocalePrefix = "education.courseduration.error.end",
            nonExistingDateLocalePrefix = "education.courseduration.real",
            alphasDateLocalePrefix = "education.courseduration.alpha",
            currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_END_DATE)
    @DynamicDateRangeConstraint(yearsUntil = END_DATE_MAX_YEARS_UNTIL, yearsAgo = END_DATE_MIN_YEARS_AGO,
            lowLimitMessage = "education.courseduration.error.min.enddate",
            highLimitMessage = "education.courseduration.error.max.enddate",
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getEndDate() {
        return this.endDate;
    }
}
