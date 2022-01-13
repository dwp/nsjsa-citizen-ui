package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.NotNull;

/**
 * EQ5 The claimants eligibility form for Actively Looking For Work.
 */
@NotNull
public class ActivelyLookingWorkQuestion implements Question {

    @NotNull
    private Boolean activelyLookingForWork;

    public Boolean getActivelyLookingForWork() {
        return activelyLookingForWork;
    }

    public void setActivelyLookingForWork(final Boolean activelyLookingForWork) {
        this.activelyLookingForWork = activelyLookingForWork;
    }
}
