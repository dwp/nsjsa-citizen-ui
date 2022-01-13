package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmailTest {

    public static final String VALID_EMAIL = "dav@dav.com";
    public static final String INVALID_EMAIL = "dav@.com";
    private static final String EMPTY_EMAIL = "";
    public static final String NULL_EMAIL = null;

    private Email email;
    private boolean isValid;

    @Test
    public void isValidReturnsTrue() {
        givenAnEmail(VALID_EMAIL);
        whenICallIsValid();
        thenTheEmailIsValid();
    }

    @Test
    public void isValidReturnsFaseForInvalidEmail() {
        givenAnEmail(INVALID_EMAIL);
        whenICallIsValid();
        thenTheEmailIsNotValid();
    }

    @Test
    public void isValidReturnsFaseForEmptyString() {
        givenAnEmail(EMPTY_EMAIL);
        whenICallIsValid();
        thenTheEmailIsNotValid();
    }

    @Test
    public void isValidReturnsFaseForNull() {
        givenAnEmail(NULL_EMAIL);
        whenICallIsValid();
        thenTheEmailIsNotValid();
    }

    private void givenAnEmail(final String value) {
        email = new Email(value);
    }

    private void whenICallIsValid() {
        isValid = email.isValid();
    }

    private void thenTheEmailIsValid() {
        assertThat(isValid, is(true));
    }

    private void thenTheEmailIsNotValid() {
        assertThat(isValid, is(false));
    }

}
