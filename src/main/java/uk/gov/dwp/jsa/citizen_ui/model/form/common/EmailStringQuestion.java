package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.EmailConstraint;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

@EmailConstraint
public class EmailStringQuestion implements Question {

    private String email;

    private Boolean hasProvidedEmail;

    public EmailStringQuestion() {
        email = "";
        hasProvidedEmail = null;
    }

    public EmailStringQuestion(final String email, final Boolean hasProvidedEmail) {
        this.email = email;
        this.hasProvidedEmail = hasProvidedEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        if (hasProvidedEmail != null && !hasProvidedEmail) {
            this.email = EMPTY;
        } else {
            this.email = email;
        }
    }

    public Boolean getHasProvidedEmail() {
        if (hasProvidedEmail == null) {
            if (email != null && !email.isEmpty()) {
                return true;
            }
        }
        return hasProvidedEmail;
    }

    public void setHasProvidedEmail(final Boolean hasProvidedEmail) {
        this.hasProvidedEmail = hasProvidedEmail;
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
