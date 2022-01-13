package uk.gov.dwp.jsa.citizen_ui.model.form.previousemployment.employerdetails;

import uk.gov.dwp.jsa.citizen_ui.model.form.AbstractCounterForm;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.CountConstraint;

import javax.validation.Valid;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static uk.gov.dwp.jsa.citizen_ui.Constants.MAX_JOBS_ALLOWED;

/**
 * Q42a, Q42b Previous Employer Details - Why Job Ended.
 */
@CountConstraint()
public class WhyJobEndForm extends AbstractCounterForm<WhyJobEndQuestion> {

    public WhyJobEndForm() {
        setMaxCount(MAX_JOBS_ALLOWED);
    }

    /**
     * Q42 Why Job Ended Reason Among the Given Options.
     */
    @Valid
    private WhyJobEndQuestion whyJobEndQuestion;

    public WhyJobEndQuestion getWhyJobEndQuestion() {
        return whyJobEndQuestion;
    }

    public void setWhyJobEndQuestion(final WhyJobEndQuestion whyJobEndQuestion) {
        this.whyJobEndQuestion = whyJobEndQuestion;
    }

    @Override
    public WhyJobEndQuestion getQuestion() {
        return getWhyJobEndQuestion();
    }

    @Override
    public void setQuestion(final WhyJobEndQuestion question) {
        setWhyJobEndQuestion(question);
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
