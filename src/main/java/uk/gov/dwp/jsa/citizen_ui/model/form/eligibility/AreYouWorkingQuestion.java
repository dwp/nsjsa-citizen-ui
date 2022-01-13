package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

import javax.validation.constraints.NotNull;

/**
 * EQ3 The claimants eligibility form for Are You Working.
 */
@NotNull
public class AreYouWorkingQuestion extends BooleanQuestion {

    public Boolean getAreYouWorking() {
        return getChoice();
    }

    public void setAreYouWorking(final Boolean areYouWorking) {
        setChoice(areYouWorking);
    }
}
