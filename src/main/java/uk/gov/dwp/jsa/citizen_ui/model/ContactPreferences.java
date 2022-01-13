package uk.gov.dwp.jsa.citizen_ui.model;

import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class ContactPreferences {

    /**
     * Q12a Contact preferences - telephone number question.
     */
    private PhoneQuestion contactPhoneQuestion;
    /**
     * Q12b Contact Preferences - Email address guard question.
     */
    private BooleanQuestion emailConfirmation;
    /**
     * Q12c Contact Preferences - email question.
     */
    private StringQuestion emailQuestion;

    public ContactPreferences(final BooleanQuestion emailConfirmation,
                              final PhoneQuestion contactPhoneQuestion,
                              final StringQuestion emailQuestion) {
        this.emailConfirmation = emailConfirmation;
        this.contactPhoneQuestion = contactPhoneQuestion;
        this.emailQuestion = emailQuestion;
    }

    public ContactPreferences() {
    }

    public BooleanQuestion getEmailConfirmation() {
        if (emailConfirmation == null) {
            this.emailConfirmation = new BooleanQuestion();
        }
        return emailConfirmation;
    }

    public void setEmailConfirmation(final BooleanQuestion emailConfirmation) {
        this.emailConfirmation = emailConfirmation;
    }

    public PhoneQuestion getContactPhoneQuestion() {
        if (contactPhoneQuestion == null) {
            contactPhoneQuestion = new PhoneQuestion();
        }
        return contactPhoneQuestion;
    }

    public void setContactPhoneQuestion(final PhoneQuestion contactPhoneQuestion) {
        this.contactPhoneQuestion = contactPhoneQuestion;
    }

    public StringQuestion getEmailQuestion() {
        if (emailQuestion == null) {
            this.emailQuestion = new StringQuestion();
        }
        return emailQuestion;
    }

    public void setEmailQuestion(final StringQuestion emailQuestion) {
        this.emailQuestion = emailQuestion;
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
