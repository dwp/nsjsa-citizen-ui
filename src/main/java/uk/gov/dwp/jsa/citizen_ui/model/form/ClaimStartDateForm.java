package uk.gov.dwp.jsa.citizen_ui.model.form;

import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EmptyStringConstraint;

import javax.validation.Valid;

/**
 * Q3 The claimants response to the claim start form.
 */
public class ClaimStartDateForm extends AbstractForm<ClaimStartDateQuestion> {

    public static final String HONEYPOT_FIELD = "dateOfBirth";

    /**
     * The Claim Start question response.
     */
    @Valid
    private ClaimStartDateQuestion claimStartDateQuestion;

    /**
     * Honeypot.
     */
    @EmptyStringConstraint
    private String dateOfBirth;

    public ClaimStartDateQuestion getClaimStartDateQuestion() {
        return getQuestion();
    }

    public void setClaimStartDateQuestion(final ClaimStartDateQuestion claimStartDateQuestion) {
        this.claimStartDateQuestion = claimStartDateQuestion;
    }

    @Override
    public ClaimStartDateQuestion getQuestion() {
        return claimStartDateQuestion;
    }

    @Override
    public void setQuestion(final ClaimStartDateQuestion question) {
        this.claimStartDateQuestion = question;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
