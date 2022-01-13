package uk.gov.dwp.jsa.citizen_ui.validation;

import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

@RunWith(JUnitParamsRunner.class)
public class EmailValidatorTest {
    private static final String VALID_EMAIL = "valid@email.com";
    private static final String INVALID_EMAIL = "not+real.com";
    private static final String VALID_EMAIL_WITH_SPACES = " valid@ email.com ";
    private static final String LONG_EMAIL =
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                    "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@.com";
    private EmailSanitiser emailSanitiser = new EmailSanitiser();
    EmailValidator emailValidator = new EmailValidator(emailSanitiser);
    private ConstraintValidatorContext mockContext = Mockito.mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

   @Test
    public void GivenInvalidEmail_ReturnsFalse() {
       when(mockContext.buildConstraintViolationWithTemplate(anyString())).
               thenReturn(mockBuilder);
       when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
               ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

       EmailStringQuestion emailQuestion = new EmailStringQuestion(INVALID_EMAIL,true);
       boolean actual = emailValidator.isValid(emailQuestion, mockContext);
       assertThat(actual, is(false));
    }

    @Test
    public void GivenLongEmail_ReturnsFalse() {
        EmailStringQuestion emailQuestion = new EmailStringQuestion(LONG_EMAIL,true);
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        boolean actual = emailValidator.isValid(emailQuestion, mockContext);
        assertThat(actual, is(false));
    }

    @Test
    public void GivenValidEmailWithSpaces_returnsTrue() {
       EmailStringQuestion emailStringQuestion = new EmailStringQuestion(VALID_EMAIL_WITH_SPACES, true);
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        boolean actual = emailValidator.isValid(emailStringQuestion, mockContext);
        assertThat(actual, is(true));
    }

    @Test
    public void GivenValidEmail_returnsTrue() {
       EmailStringQuestion emailStringQuestion = new EmailStringQuestion(VALID_EMAIL, true);
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        boolean actual = emailValidator.isValid(emailStringQuestion, mockContext);
        assertThat(actual, is(true));
    }
}
