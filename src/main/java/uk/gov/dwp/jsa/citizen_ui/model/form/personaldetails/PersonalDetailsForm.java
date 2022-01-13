package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;

/**
 * The class is for storing personal details about the claimant.
 */
public class PersonalDetailsForm extends AbstractForm<PersonalDetailsQuestion> {

    @Valid
    private PersonalDetailsQuestion personalDetailsQuestion;

    public PersonalDetailsQuestion getPersonalDetailsQuestion() {
        return personalDetailsQuestion;
    }

    public void setPersonalDetailsQuestion(final PersonalDetailsQuestion personalDetailsQuestion) {
        this.personalDetailsQuestion = personalDetailsQuestion;
    }

    @Override
    public PersonalDetailsQuestion getQuestion() {
        return getPersonalDetailsQuestion();
    }

    @Override
    public void setQuestion(final PersonalDetailsQuestion question) {
        setPersonalDetailsQuestion(question);
    }
}
