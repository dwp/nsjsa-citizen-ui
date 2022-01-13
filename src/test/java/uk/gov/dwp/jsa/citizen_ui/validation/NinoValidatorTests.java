package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.gov.dwp.jsa.citizen_ui.App;
import uk.gov.dwp.jsa.citizen_ui.model.form.personaldetails.NinoQuestion;
import uk.gov.dwp.jsa.citizen_ui.services.NinoSanitiser;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class NinoValidatorTests {
    private final String CORRECTLY_FORMATTED_NINO = "AB123456A";
    private final String CORRECTLY_FORMATTED_NINO_WITH_SPACES = "AB123456A";
    private final String INCORRECTLY_FORMATTED_LARGE_NINO = "AB1234567A";
    private final String EMPTY_NINO = "";
    private final List<String> FIRST_CHARACTERS_INVALID = Arrays.asList("D", "F", "I", "Q", "U", "V");
    private final List<String> SECOND_CHARACTERS_INVALID = FIRST_CHARACTERS_INVALID;
    private final List<String> FIRST_TWO_CHARACTER_COMBINATIONS_INVALID = Arrays.asList("GB", "NK", "TN", "ZZ");
    private final List<String> LAST_CHARACTERS_VALID = Arrays.asList("A", "B", "C", "D");

    private NinoSanitiser ninoSanitiser = new NinoSanitiser();
    NinoValidator ninoValidator = new NinoValidator(ninoSanitiser);
    ConstraintValidatorContext mockContext = Mockito.mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @Test
    public void givenACorrectlySanitisedNino_returnValid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        NinoQuestion ninoQuestion = new NinoQuestion();
        ninoQuestion.setValue(CORRECTLY_FORMATTED_NINO);
        boolean actual = ninoValidator.isValid(ninoQuestion.getValue(), mockContext);
        assertThat(actual, is(true));
    }

    @Test
    public void givenACorrectlySanitisedNinoWithSpaces_returnValid() {
        boolean actual = ninoValidator.isValid(CORRECTLY_FORMATTED_NINO_WITH_SPACES, mockContext);
        assertThat(actual, is(true));
    }


    @Test
    public void givenAnIncorrectlyFormattedNino_returnInvalid() {
        boolean actual = ninoValidator.isValid(INCORRECTLY_FORMATTED_LARGE_NINO, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void givenAnEmptyNino_returnInvalid() {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
        boolean actual = ninoValidator.isValid(EMPTY_NINO, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void givenANinoWithAnIncorrectFirstCharacter_returnInvalid() {
        FIRST_CHARACTERS_INVALID.forEach(nino -> {
            boolean actual = ninoValidator.isValid(nino, mockContext);
            assertThat(actual, is(false));
        });
    }

    @Test
    public void givenANinoWithAnIncorrectSecondCharacter_returnInvalid() {
        SECOND_CHARACTERS_INVALID.forEach(nino -> {
            boolean actual = ninoValidator.isValid(nino, mockContext);
            assertThat(actual, is(false));
        });
    }

    @Test
    public void givenANinoWithTheFirstTwoCharactersIncorrect_returnInvalid() {
        FIRST_TWO_CHARACTER_COMBINATIONS_INVALID.forEach(nino -> {
            boolean actual = ninoValidator.isValid(nino, mockContext);
            assertThat(actual, is(false));
        });
    }

    @Test
    public void givenANinoWithAnIncorrectLastCharacter_returnInvalid() {
        LAST_CHARACTERS_VALID.forEach(nino -> {
            boolean actual = ninoValidator.isValid(nino, mockContext);
            assertThat(actual, is(false));
        });
    }
}
