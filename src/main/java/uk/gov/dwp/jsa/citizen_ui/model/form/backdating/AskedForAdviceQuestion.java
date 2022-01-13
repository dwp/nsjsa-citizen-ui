package uk.gov.dwp.jsa.citizen_ui.model.form.backdating;


import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.HasHadAdviceConstraint;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Question for backdating asked for advice.
 */
@HasHadAdviceConstraint
public class AskedForAdviceQuestion implements Question {
    private Boolean hasHadAdvice;
    private String value;

    public AskedForAdviceQuestion() {
        value = "";
        hasHadAdvice = null;
    }

    public AskedForAdviceQuestion(final String value, final Boolean hasHadAdvice) {
        this.value = value;
        this.hasHadAdvice = hasHadAdvice;
    }

    public void setValue(final String value) {
        if (hasHadAdvice != null && !hasHadAdvice) {
            this.value = EMPTY;
        } else {
            this.value = value;
        }
    }

    public String getValue() {
        return value;
    }

    public Boolean getHasHadAdvice() {
        if (hasHadAdvice == null) {
            if (value != null && !value.isEmpty()) {
                return true;
            }
        }
        return hasHadAdvice;
    }

    public void setHasHadAdvice(final Boolean hasHadAdvice) {
        this.hasHadAdvice = hasHadAdvice;
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
