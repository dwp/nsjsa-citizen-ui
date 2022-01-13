package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PhoneNumberTest {

    private static final String NULL_PHONE_NUMBER = null;
    private static final String EMPTY_PHONE_NUMBER = "";
    private static final String NON_MOBILE_PHONE_NUMBER = "0123";
    private static final String MOBILE_PHONE_NUMBER = "077";

    private PhoneNumber phoneNumber;
    private boolean isValid;
    private boolean isMobile;

    @Test
    public void isValidReturnsFalseForNull() {
        givenANumber(NULL_PHONE_NUMBER);
        whenICallIsValid();
        thenTheNumberIsNotValid();
    }

    @Test
    public void isValidReturnsFalseForEmptyString() {
        givenANumber(EMPTY_PHONE_NUMBER);
        whenICallIsValid();
        thenTheNumberIsNotValid();
    }

    @Test
    public void isValidReturnsTrue() {
        givenANumber(NON_MOBILE_PHONE_NUMBER);
        whenICallIsValid();
        thenTheNumberIsValid();
    }

    @Test
    public void isMobileReturnsTrue() {
        givenANumber(MOBILE_PHONE_NUMBER);
        whenICallIsMobile();
        thenTheNumberIsMobile();
    }

    @Test
    public void isMobileReturnsFalse() {
        givenANumber(NON_MOBILE_PHONE_NUMBER);
        whenICallIsValid();
        thenTheNumberIsNotMobile();
    }

    private void givenANumber(final String value) {
        phoneNumber = new PhoneNumber(value);
    }

    private void whenICallIsValid() {
        isValid = phoneNumber.isValid();
    }

    private void whenICallIsMobile() {
        isMobile = phoneNumber.isMobile();
    }

    private void thenTheNumberIsNotValid() {
        assertThat(isValid, is(false));
    }

    private void thenTheNumberIsValid() {
        assertThat(isValid, is(true));
    }

    private void thenTheNumberIsNotMobile() {
        assertThat(isMobile, is(false));
    }

    private void thenTheNumberIsMobile() {
        assertThat(isMobile, is(true));
    }

}
