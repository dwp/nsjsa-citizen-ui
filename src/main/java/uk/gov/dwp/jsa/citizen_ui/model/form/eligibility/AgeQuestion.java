package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;

import javax.validation.constraints.NotNull;

/**
 * EQ2 The claimants eligibility form for age over 18.
 */
public class AgeQuestion implements Question {

    /**
     * EQ2 The claimants eligibility answer for age over 18.
     */
    @NotNull
    private Boolean overEighteen;

    public Boolean getOverEighteen() {
        return overEighteen;
    }

    public void setOverEighteen(final Boolean overEighteen) {
        this.overEighteen = overEighteen;
    }
}
