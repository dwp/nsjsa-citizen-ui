package uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.JobEndDetailedReasonConstraint;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Q42a Q42b Previous Employer Details - Why Job Ended.
 */
@JobEndDetailedReasonConstraint
public class WhyJobEndQuestion implements Question {

    public static final String ERROR_MESSAGE = "previousemployment.employerdetails.whyended.error.empty";
    public static final String ERROR_MESSAGE_EMPTY = "previousemployment.employerdetails.whyended.error.other.empty";
    public static final String ERROR_MESSAGE_INVALID =
            "previousemployment.employerdetails.whyended.error.other.invalid";

    /**
     * Q42a Previous Employer Details - Why Job Ended.
     */
    private WhyJobEndedReason whyJobEndedReason;

    /**
     * Q42b Detailed Reason Why Job Ended.
     */
    private String detailedReason;

    public WhyJobEndedReason getWhyJobEndedReason() {
        return whyJobEndedReason;
    }

    public void setWhyJobEndedReason(final WhyJobEndedReason whyJobEndedReason) {
        this.whyJobEndedReason = whyJobEndedReason;
    }

    public String getDetailedReason() {
        return detailedReason;
    }

    public void setDetailedReason(final String detailedReason) {
        this.detailedReason = detailedReason;
    }

    @Override
    public boolean equals(final Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
