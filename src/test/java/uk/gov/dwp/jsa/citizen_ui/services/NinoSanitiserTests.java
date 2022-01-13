package uk.gov.dwp.jsa.citizen_ui.services;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {App.class})
public class NinoSanitiserTests {

    final NinoSanitiser ninoSanitiser = new NinoSanitiser();

    @Test
    @Parameters({
            "AB123456A", // correctly formatted Nino
            "AB 12 34 56 A", // nicely formatted Nino
            "AB    12 34  56    A", // nino with random spaces
            "ab 12 34 56 a", // nino with lower case characters
            "Ab 12 34 56 a" // nino with mixed cases
    })
    public void GivenAnyNino_ReturnSanitisedNino(String nino) {
        String actual = ninoSanitiser.sanitise(nino);
        assertThat(actual, is("AB123456A"));
    }

    @Test
    public void givenNinoWhichIsEmpty_returnEmptyNino() {
        String expected = "";
        String actual = ninoSanitiser.sanitise(expected);
        assertThat(actual, is(expected));
    }
}
