package uk.gov.dwp.jsa.citizen_ui.model.form.claimstart;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.DateRangeQuestion;
import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.DynamicDateRangeConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.PeriodConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

@PeriodConstraint(message = "juryservice.error.startdate.after.enddate",
        groups = ValidationSequence.BusinessValidationGroup.class)
public class JuryServiceDurationQuestion extends DateRangeQuestion {

    public JuryServiceDurationQuestion(final DateQuestion startDate, final DateQuestion endDate) {
        super(startDate, endDate);
    }

    public JuryServiceDurationQuestion() {
    }

    @Override
    @ValidDateConstraint(message = "juryservice.dates.error.empty.start",
            nonExistingDateLocalePrefix = "juryservice.dates.error.real",
            alphasDateLocalePrefix = "juryservice.dates.error.alpha",
            currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_START_DATE)
    @DynamicDateRangeConstraint(lowLimitMessage = "juryservice.error.min.startdate",
            highLimitMessage = "juryservice.error.max.startdate",
            yearsAgo = 1,
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getStartDate() {
        return this.startDate;
    }

    @Override
    @ValidDateConstraint(message = "juryservice.dates.error.empty.end",
            nonExistingDateLocalePrefix = "juryservice.dates.error.real",
            alphasDateLocalePrefix = "juryservice.dates.error.alpha",
            currentDateQuestion = DateValidator.DateQuestionIdentify.DATE_RANGE_END_DATE)
    @DynamicDateRangeConstraint(highLimitMessage = "juryservice.error.max.enddate",
            lowLimitMessage = "juryservice.error.min.enddate",
            yearsAgo = 1,
            groups = ValidationSequence.BusinessValidationGroup.class)
    public DateQuestion getEndDate() {
        return this.endDate;
    }
}
