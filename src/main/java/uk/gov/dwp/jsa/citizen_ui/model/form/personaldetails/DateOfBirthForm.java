package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;
import uk.gov.dwp.jsa.citizen_ui.validation.DateValidator;
import uk.gov.dwp.jsa.citizen_ui.validation.ValidationSequence;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.DateofBirthConstraint;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.common.ValidDateConstraint;

import static uk.gov.dwp.jsa.citizen_ui.model.form.error.DateofBirthConditionsEnum.BETWEEN_16_17;

/**
 * The claimants response to the date of birth form.
 */
public class DateOfBirthForm extends AbstractForm<DateOfBirthQuestion> {

    /**
     * The date of birth question response.
     */
    @ValidDateConstraint(message = "dateofbirth.error.blank",
                        incompleteStartDateLocalePrefix = "dateofbirth.error.start",
                        nonExistingSingleDateLocale = "dateofbirth.field.error",
                        alphasDateLocalePrefix = "dateofbirth.error.alpha",
                        currentDateQuestion = DateValidator.DateQuestionIdentify.NON_DATE_RANGE_DATE_OF_BIRTH)
    @DateofBirthConstraint(groups = ValidationSequence.BusinessValidationGroup.class)
    private DateOfBirthQuestion dateOfBirthQuestion;

    public DateOfBirthForm(final DateOfBirthQuestion dateOfBirthQuestion) {
        this.dateOfBirthQuestion = dateOfBirthQuestion;
    }

    public DateOfBirthForm() {
    }

    public DateOfBirthQuestion getDateOfBirthQuestion() {
        return getQuestion();
    }

    public void setDateOfBirthQuestion(
            final DateOfBirthQuestion dateOfBirthQuestion) {
        this.dateOfBirthQuestion = dateOfBirthQuestion;
    }

    @Override
    public boolean isAGuard() {
        return true;
    }

    @Override
    public boolean isGuardedCondition() {
        return (dateOfBirthQuestion != null)
                && BETWEEN_16_17.equals(dateOfBirthQuestion.getDateofBirthConditionsEnum());
    }

    @Override
    public DateOfBirthQuestion getQuestion() {
        return dateOfBirthQuestion;
    }

    @Override
    public void setQuestion(final DateOfBirthQuestion question) {
        setDateOfBirthQuestion(question);
    }
}
