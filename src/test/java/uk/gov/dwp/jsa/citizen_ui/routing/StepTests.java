package uk.gov.dwp.jsa.citizen_ui.routing;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.jsa.citizen_ui.controller.Section;
import uk.gov.dwp.jsa.citizen_ui.model.form.Form;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(JUnitParamsRunner.class)
public class StepTests {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private String anonymousIdentifier = "testidentifier";

    @Mock
    private Form mockForm;

    private String anonymousNextIdentifier = "anonymousNextIdentifier";
    private String anonymousAlternateIdentifier = "anonymousAlternateIdentifier";

    @Test(expected = IllegalArgumentException.class)
    public void constructor_doesNotAcceptNullIdentifier() {

        createSut((String)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_doesNotAcceptEmptyIdentifier() {

        createSut("");
    }

    @Test
    public void constructor_acceptsNullNextAndAlternate() {

        Step sut = createSut(null, null);
        assertThat(sut, notNullValue());
    }

    @Test
    @Parameters({
            "residence",
            "averylonglonglongidentifier",
            "identifierno1",
            "identi-fierno100"
    })
    public void getIdentifier_returnsIdentifier(String value) {

        Step sut = createSut(value);
        String actual = sut.getIdentifier();
        assertThat(actual, is(value));
    }

    @Test
    public void getNextStepIdentifier_returnsNextStepIdentifier() {

        String expected = "expectedNextIdentifier";
        Step sut = createSut(expected, anonymousAlternateIdentifier);
        String actual = sut.getNextStepIdentifier();
        assertThat(actual, is(expected));
    }

    @Test
    public void getAlternateNextStepIdentifier_returnsAlternateNextStepIdentifier() {

        String expected = "expectedAlternateIdentifier";
        Step sut = createSut(anonymousNextIdentifier, expected);
        Optional<String> actual = sut.getNextAlternateStepIdentifier();
        assertThat(actual.get(), is(expected));
    }

    public Step createSut() {

        return createSut(anonymousIdentifier, mockForm, anonymousNextIdentifier, anonymousAlternateIdentifier);
    }

    public Step createSut(String identifier) {

        return createSut(identifier, mockForm, anonymousNextIdentifier, anonymousAlternateIdentifier);
    }

    public Step createSut(Form form) {

        return createSut(anonymousIdentifier, form, anonymousNextIdentifier, anonymousAlternateIdentifier);
    }

    public Step createSut(String identifier, String alternateIdentifier) {

        return createSut(anonymousIdentifier, mockForm, identifier, alternateIdentifier);
    }

    public Step createSut(String identifier, Form form, String nextIdentifier, String nextAlternateIdentifier) {

        return new Step(identifier, nextIdentifier, nextAlternateIdentifier, Section.NONE);
    }
}
