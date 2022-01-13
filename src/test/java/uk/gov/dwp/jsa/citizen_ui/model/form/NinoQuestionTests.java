package uk.gov.dwp.jsa.citizen_ui.model.form;

import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {App.class})
public class NinoQuestionTests {

    NinoQuestion sut;

    @Before
    public void before() {
        sut = new NinoQuestion();
    }

    @Test
    public void givenUnformattedNino_returnWellFormattedNino() {
        sut.setValue("SN123456A");
        String expectedFormat = "SN 12 34 56 A";
        String actual = sut.getFormattedNino();
        assertThat(actual, is(expectedFormat));
    }

    @Test
    public void formatsNinoWithBlankSpacesInIt() {
        sut.setValue("n c                          13783 8c");
        String expectedFormat = "nc 13 78 38 c";
        String actual = sut.getFormattedNino();
        assertThat(actual, is(expectedFormat));
    }

    @Test
    public void getFormmattedNinoWithNullReturnsNull() {
        sut.setValue(null);
        String expectedFormat = null;
        String actual = sut.getFormattedNino();
        assertThat(actual, is(expectedFormat));
    }

}
