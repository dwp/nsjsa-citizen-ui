package uk.gov.dwp.jsa.citizen_ui.model.form.eligibility;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * EQ2 The claimants eligibility form for age over 18.
 */
public class AgeForm {

    @Valid
    @NotNull
    private AgeQuestion ageQuestion;

    public AgeQuestion getAgeQuestion() {
        return ageQuestion;
    }

    public void setAgeQuestion(final AgeQuestion ageQuestion) {
        this.ageQuestion = ageQuestion;
    }
}
