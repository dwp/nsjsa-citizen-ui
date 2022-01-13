package uk.gov.dwp.jsa.citizen_ui.services;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {App.class})
public class PhoneSanitiserTests {
    private final PhoneSanitiser sanitiser= new PhoneSanitiser();

    @Test
    @Parameters({
            "07715009000",
            "0771 5009000",
            "(077) 1500(9000)",
            "07-715-009-000",
            "07 (715) 00-9000",
            "0771 5009000",
            "0771 500 9000-",
    })
    public void GivenAnyPhoneNumber_ReturnSanitisedPhoneNumber(String phone) {
        String actual = sanitiser.sanitise(phone);
        assertThat(actual, is("07715009000"));
    }

    @Test
    public void GivenEmptyPhoneNumber_ReturnEmptyPhoneNumber() {
        String expected = "";
        String actual = sanitiser.sanitise(expected);
        assertThat(actual, is(expected));
    }

    @Test
    public void GivenNullPhoneNumber_ReturnEmptyPhoneNumber() {
        String expected = null;
        String actual = sanitiser.sanitise(expected);
        assertNull(actual);
    }
}
