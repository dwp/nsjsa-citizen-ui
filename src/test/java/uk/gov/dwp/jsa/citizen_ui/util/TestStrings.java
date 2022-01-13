package uk.gov.dwp.jsa.citizen_ui.util;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestStrings {
    private static final String LONG_NAME =    "AAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String TRIMMED_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    @Test
    public void getTrimmedStringTrimsTo27Length() {
        assertThat(Strings.truncate(LONG_NAME, 27), is(TRIMMED_NAME));
    }

    @Test
    public void getTrimmedStringWithShortLengthReturnsSameString() {
        String testName = "TestName";
        assertThat(Strings.truncate(testName, 27), is(testName));
    }

    @Test
    public void truncateShouldReturnNullIfPassedNull() {
        assertThat(Strings.truncate(null, 27), CoreMatchers.nullValue());
    }
}
