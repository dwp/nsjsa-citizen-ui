package uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails;

import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {App.class})
public class DateOfBirthQuestionTest {

    private DateOfBirthQuestion sut;

    @Before
    public void before() {
        sut = new DateOfBirthQuestion();
    }

    @Test
    public void givenUnformatteDateOfBirth_returnWellFormatteDateOfBirth() {
        sut.setDay(31);
        sut.setYear(1980);
        sut.setMonth(12);
        String expectedFormat = "31/12/1980";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));
    }

    @Test
    public void givenUnformatteInvalidDateOfBirth_failGracefully() {
        sut.setYear(1980);
        sut.setMonth(12);
        String expectedFormat = "";
        String actual = sut.getFormattedValue();
        assertThat(actual, is(expectedFormat));
    }

}
