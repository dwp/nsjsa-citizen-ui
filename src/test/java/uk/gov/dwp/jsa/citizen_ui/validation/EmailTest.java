package uk.gov.dwp.jsa.citizen_ui.validation;

import com.sun.istack.NotNull;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailForm;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.EmailStringQuestion;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.StringForm;
import uk.gov.dwp.jsa.citizen_ui.services.EmailSanitiser;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class EmailTest {
    private EmailSanitiser emailSanitiser = new EmailSanitiser();
    private EmailValidator emailValidator = new EmailValidator(emailSanitiser);
    private ConstraintValidatorContext mockContext = Mockito.mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    /**
     * These test valid values of emails.
     * @param email - the email to be validated.
     */
    @Parameters({"my@test.com",
                        "m@t.co",
                        "+@-.uk",
                        "qTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSv"
                        + "tcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFm"
                        + "OwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqT"
                        + "fBPMAZyijzNhXA@n.se"
                })
    @Test
    public void validEmailShouldValidate(String email) {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
        EmailForm form = getEmailForm(email);
        boolean actual = emailValidator.isValid(form.getEmailStringQuestion(), mockContext);
        assertThat(actual, is(true));
    }

    @Parameters({"",
                        "my@test.o",
                        "m$@t.co",
                        "qTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGl" +
                                "SvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNha" +
                                "XFmOwEuobHWkxDQLrGlSvtcJVpURKaCYIqTfBPMAZyijzNhXFmOwEuobHWkxDQLrGlSvtcJVpURKaCYI" +
                                "qTfBPMAZyijzNhXA@n.se"
                })
    @Test
    public void invalidEmailShouldBindErrors(String email) {
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).
                thenReturn(mockBuilder);
        when(mockBuilder.addPropertyNode(anyString())).thenReturn(mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        EmailForm form = getEmailForm(email);
        boolean actual = emailValidator.isValid(form.getEmailStringQuestion(), mockContext);
        assertThat(actual, is(false));
    }

    @NotNull
    public EmailForm getEmailForm(final String email) {
        EmailStringQuestion emailStringQuestion = new EmailStringQuestion(email, true);
        EmailForm emailForm = new EmailForm(emailStringQuestion);
        return emailForm;
    }


}
