package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;

import uk.gov.dwp.jsa.citizen_ui.validation.BooleanWithHiddenDateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.BooleanWithHiddenDateConstraint;

@BooleanWithHiddenDateConstraint(
        missingYesNoAnswerLocal = "backdating.have.you.travelled.outside.mandatory",
        missingStartDateLocal = "backdating.have.you.travelled.outside.startdate.mandatory",
        missingEndDateLocal = "backdating.have.you.travelled.outside.enddate.mandatory",
        startDateMustBeAfterMinimumAllowedDateLocal = "backdating.have.you.travelled.outside.after.claim.start.date.startdate",
        endDateMustBeAfterMinimumAllowedDateLocal = "backdating.have.you.travelled.outside.after.claim.start.date.enddate",
        startDateIsAfterEndDateLocal = "backdating.have.you.travelled.outside.startdate.after.enddate",
        startDateMustBeInThePastLocal = "backdating.have.you.travelled.outside.startdate.future",
        endDateMustBeInThePastLocal = "backdating.have.you.travelled.outside.enddate.future",
        questionIdentifier = BooleanWithHiddenDateValidator.QuestionIdentifier.TRAVELED_OUTSIDE_UK,
        startDateMustBeReal = "backdating.have.you.travelled.outside.real",
        dateCantBeAlpha = "backdating.have.you.travelled.outside.alpha"
)
public class HaveYouTravelledOutsideQuestion extends BooleanAndDateFieldQuestions {

    public HaveYouTravelledOutsideQuestion(final Boolean hasProvidedAnswer, final DateRangeQuestionWithBoolean dateRangeQuestion) {
      super(hasProvidedAnswer, dateRangeQuestion);
    }

    public HaveYouTravelledOutsideQuestion() {
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
    public void setHasProvidedAnswer(final Boolean hasProvidedAnswer) {
        this.hasProvidedAnswer = hasProvidedAnswer;
    }

    @Override
    public void setDateRangeQuestion(final DateRangeQuestionWithBoolean dateRangeQuestion) {
        if (this.hasProvidedAnswer != null && !this.hasProvidedAnswer) {
            this.dateRangeQuestion = new DateRangeQuestionWithBoolean();
        } else {
            this.dateRangeQuestion = dateRangeQuestion;
        }
    }
}
