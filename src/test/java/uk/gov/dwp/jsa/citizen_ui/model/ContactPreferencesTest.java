package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.BooleanQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.PhoneQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringQuestion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ContactPreferencesTest {

    public static final BooleanQuestion EMAIL_CONFIRMATION = new BooleanQuestion();
    public static final PhoneQuestion CONTACT_PHONE_QUESTION = new PhoneQuestion();
    public static final StringQuestion EMAIL_QUESTION = new StringQuestion();

    @Test
    public void constructorSetsFieldValues() {
        final ContactPreferences contactPreferences = new ContactPreferences(
                EMAIL_CONFIRMATION, CONTACT_PHONE_QUESTION, EMAIL_QUESTION);
        assertThat(contactPreferences.getEmailConfirmation(), is(EMAIL_CONFIRMATION));
        assertThat(contactPreferences.getContactPhoneQuestion(), is(CONTACT_PHONE_QUESTION));
        assertThat(contactPreferences.getEmailQuestion(), is(EMAIL_QUESTION));
    }


    @Test
    public void hasDefaultValues() {
        final ContactPreferences contactPreferences = new ContactPreferences();
        assertThat(contactPreferences.getEmailConfirmation(), is(EMAIL_CONFIRMATION));
        assertTrue(contactPreferences.getContactPhoneQuestion().equals(CONTACT_PHONE_QUESTION));
        assertThat(contactPreferences.getEmailQuestion(), is(EMAIL_QUESTION));
    }


    @Test
    public void setEmailConfirmation() {
        final ContactPreferences contactPreferences = new ContactPreferences();
        contactPreferences.setEmailConfirmation(EMAIL_CONFIRMATION);
        assertThat(contactPreferences.getEmailConfirmation(), is(EMAIL_CONFIRMATION));
    }

    @Test
    public void setContactPhoneQuestion() {
        final ContactPreferences contactPreferences = new ContactPreferences();
        contactPreferences.setContactPhoneQuestion(CONTACT_PHONE_QUESTION);
        assertThat(contactPreferences.getContactPhoneQuestion(), is(CONTACT_PHONE_QUESTION));
    }

    @Test
    public void setEmailQuestion() {
        final ContactPreferences contactPreferences = new ContactPreferences();
        contactPreferences.setEmailQuestion(EMAIL_QUESTION);
        assertThat(contactPreferences.getEmailQuestion(), is(EMAIL_QUESTION));
    }
}
