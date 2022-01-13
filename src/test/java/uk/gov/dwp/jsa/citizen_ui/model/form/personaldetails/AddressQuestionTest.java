package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.AddressQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.about.PostalAddressQuestion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AddressQuestionTest {

    private AddressQuestion sut;

    @Before
    public void before() {
        sut = new PostalAddressQuestion();
    }

    @Test
    public void givenUnformattedAddress_returnWellFormattedAddress() {
        sut.setAddressLine1("1 sample lane");
        sut.setTownOrCity("sampletown");
        sut.setPostCode("sa47sh");

        String expectedFormat = "1 sample lane, sampletown, sa47sh";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));

    }

    @Test
    public void givenUnformattedAddressWithoutCountry_returnWellFormattedAddress() {
        sut.setAddressLine1("1 sample lane");
        sut.setAddressLine2("Second Address Line");
        sut.setPostCode("sa47SH");
        sut.setTownOrCity("SAMPLETOWN");

        String expectedFormat = "1 sample lane, Second Address Line, SAMPLETOWN, sa47SH";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));

    }

    @Test
    public void givenUnformattedInvalidAddress_returnWellFormattedAddress() {
        String expectedFormat = "";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));
    }

    @Test
    public void givenUnformattedEmptyAddress_returnWellFormattedAddress() {
        sut.setAddressLine1("  ");
        sut.setTownOrCity("  ");
        sut.setPostCode("  ");

        String expectedFormat = "";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));
    }
}
