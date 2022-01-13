package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;

/**
 * EQ1 Claimants residence question for eligibility.
 */
public class ResidenceQuestion extends BooleanQuestion {

    /**
     * EQ1 Claimants residence answer for eligibility.
     */

    public Boolean getUkResidence() {
        return getChoice();
    }

    public void setUkResidence(final Boolean ukResidence) {
        setChoice(ukResidence);
    }
}
