package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ActivelyLookingWorkForm {

    @Valid
    @NotNull
    private ActivelyLookingWorkQuestion activelyLookingWorkQuestion;

    public ActivelyLookingWorkQuestion getActivelyLookingWorkQuestion() {
        return activelyLookingWorkQuestion;
    }

    public void setActivelyLookingWorkQuestion(final ActivelyLookingWorkQuestion activelyLookingWorkQuestion) {
        this.activelyLookingWorkQuestion = activelyLookingWorkQuestion;
    }
}
