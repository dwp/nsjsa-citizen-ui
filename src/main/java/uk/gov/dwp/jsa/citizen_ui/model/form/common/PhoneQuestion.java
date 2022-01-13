package uk.gov.dwp.jsa.citizen_ui.model.form.common;

import uk.gov.dwp.jsa.citizen_ui.model.form.Question;
import uk.gov.dwp.jsa.citizen_ui.validation.constraints.UKPhoneNumberConstraint;

import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

/**
 * Question for Telephone Number.
 */
@UKPhoneNumberConstraint
public class PhoneQuestion implements Question {

    private String phoneNumber;

    private Boolean hasProvidedPhoneNumber;


    public PhoneQuestion() {
        phoneNumber = "";
        hasProvidedPhoneNumber = null;
    }

    public PhoneQuestion(final String phoneNumber, final Boolean hasProvidedPhoneNumber) {
      this.phoneNumber = phoneNumber;
      this.hasProvidedPhoneNumber = hasProvidedPhoneNumber;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        if (hasProvidedPhoneNumber != null && !hasProvidedPhoneNumber) {
            this.phoneNumber = EMPTY;
        } else {
            this.phoneNumber = phoneNumber;
        }
    }

    public Boolean getHasProvidedPhoneNumber() {
        if (hasProvidedPhoneNumber == null) {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                return true;
            }
        }
        return hasProvidedPhoneNumber;
    }

    public void setHasProvidedPhoneNumber(final Boolean hasProvidedPhoneNumber) {
        this.hasProvidedPhoneNumber = hasProvidedPhoneNumber;
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
