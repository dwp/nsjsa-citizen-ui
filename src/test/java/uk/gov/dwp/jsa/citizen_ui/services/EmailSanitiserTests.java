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
public class EmailSanitiserTests {
    private final EmailSanitiser sanitiser = new EmailSanitiser();

    @Test
    @Parameters({
            "qTfBPMAZyijzNhXFmOw   EuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGl" +
                    "SvtcJVpURKaCYIqTfBPM   AZyijzNhXFmOwEuobHWkxDQLr  GlSvtcJVpURKaCYIqTfBPMAZyijzNha" +
                    "XFmOwEuobH WkxDQLrGlSvtcJV pURKaCYIqTfBPMAZyijzNhXFmOwEuobH WkxDQLrGlSvtcJVpURKaCYI" +
                    "qTfBPMAZyijzNh XA@n.se"

    })
    public void GivenAnyEmail_ReturnSanitisedEmail(String email) {
        String actual = sanitiser.sanitise(email);
       assertThat(actual, is(
                "qTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGl" +
                "SvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNha" +
                        "XFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYI" +
                        "qTfBPMAZyijzNhXA@n.se"));
    }

    @Test
    public void GivenEmptyEmail_ReturnEmptyEmail() {
        String expected = "";
        String actual = sanitiser.sanitise(expected);
        assertThat(actual, is(expected));
    }

    @Test
    public void GivenNullEmail_ReturnEmptyEmail() {
        String expected = null;
        String actual = sanitiser.sanitise(expected);
        assertNull(actual);
    }
}
