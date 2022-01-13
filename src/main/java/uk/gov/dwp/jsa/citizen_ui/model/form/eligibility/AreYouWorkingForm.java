package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AreYouWorkingForm  extends AbstractForm<AreYouWorkingQuestion> {

    @Valid
    @NotNull
    private AreYouWorkingQuestion areYouWorkingQuestion;

    @SuppressWarnings("squid:S2637")
    public AreYouWorkingForm() {
        // Default constructor added for SONAR Violations.
    }

    @Override
    public AreYouWorkingQuestion getQuestion() {
        return areYouWorkingQuestion;
    }

    @Override
    public void setQuestion(final AreYouWorkingQuestion areYouWorkingQuestion) {
        this.areYouWorkingQuestion = areYouWorkingQuestion;
    }
}
