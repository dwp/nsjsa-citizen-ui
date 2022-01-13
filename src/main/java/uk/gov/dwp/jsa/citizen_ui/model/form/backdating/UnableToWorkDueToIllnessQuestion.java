package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.validation.BooleanWithHiddenDateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BooleanWithHiddenDateConstraint;

@BooleanWithHiddenDateConstraint(
        missingYesNoAnswerLocal = "backdating.unable.to.work.due.to.illness.mandatory",
        missingStartDateLocal = "backdating.unable.to.work.due.to.illness.startdate.mandatory",
        missingEndDateLocal = "backdating.unable.to.work.due.to.illness.enddate.mandatory",
        startDateMustBeAfterMinimumAllowedDateLocal = "backdating.unable.to.work.due.to.illness.after.claim.start.date.startdate",
        endDateMustBeAfterMinimumAllowedDateLocal = "backdating.unable.to.work.due.to.illness.after.claim.start.date.enddate",
        startDateIsAfterEndDateLocal = "backdating.unable.to.work.due.to.illness.startdate.after.enddate",
        startDateMustBeInThePastLocal = "backdating.unable.to.work.due.to.illness.startdate.after.today",
        endDateMustBeInThePastLocal = "backdating.unable.to.work.due.to.illness.enddate.after.today",
        questionIdentifier = BooleanWithHiddenDateValidator.QuestionIdentifier.UNABLE_TO_WORK_DUE_TO_ILLNESS,
        startDateMustBeReal = "backdating.unable.to.work.due.to.illness.real",
        dateCantBeAlpha = "backdating.unable.to.work.due.to.illness.alpha")
public class UnableToWorkDueToIllnessQuestion extends BooleanAndDateFieldQuestions {

    public UnableToWorkDueToIllnessQuestion(final Boolean hasProvidedAnswer, final DateRangeQuestionWithBoolean dateRangeQuestion) {
        super(hasProvidedAnswer, dateRangeQuestion);
    }

    public UnableToWorkDueToIllnessQuestion() {
    }

    @Override
    public Boolean getHasProvidedAnswer() {
        return hasProvidedAnswer;
    }

    @Override
    public DateRangeQuestionWithBoolean getDateRangeQuestion() {
        return this.dateRangeQuestion;
    }

    @Override
    public void setDateRangeQuestion(final DateRangeQuestionWithBoolean dateRangeQuestion) {
        if (this.hasProvidedAnswer != null && !this.hasProvidedAnswer) {
            this.dateRangeQuestion = new DateRangeQuestionWithBoolean();
        } else {
            this.dateRangeQuestion = dateRangeQuestion;
        }
    }

    @Override
    public void setHasProvidedAnswer(final Boolean hasProvidedAnswer) {
        this.hasProvidedAnswer = hasProvidedAnswer;
    }
}
