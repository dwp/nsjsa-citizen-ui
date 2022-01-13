package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * EQ1 The claimants eligibility form for residence.
 */


public class ResidenceForm extends AbstractForm<ResidenceQuestion> {

    @Valid
    @NotNull
    private ResidenceQuestion residenceQuestion;

    @Override
    public ResidenceQuestion getQuestion() {
        return residenceQuestion;
    }

    @Override
    public void setQuestion(final ResidenceQuestion question) {
        residenceQuestion = question;
    }

}
