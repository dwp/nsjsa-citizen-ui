package uk.gov.dwp.jsa.citizen_ui.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WarningLoggerTest {

    private PrintStream sysOut;
    private final ByteArrayOutputStream outContent
        = new ByteArrayOutputStream();
    @Mock
    private BindingResult mockBindingResult;
    @Mock
    private ObjectError mockObjectError;
    private WarningLogger warningLogger = new WarningLogger();

    @Before
    public void setUp() {
        sysOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void revertStreams() {
        System.setOut(sysOut);
    }

    @Test
    public void logErrorIfTypeMismatch() {
        givenBindingErrorIsMockedToReturnTypeMismatch("typemiSMatch");

        warningLogger.logErrorIfTypeMismatch(mockBindingResult);

        assertThat(outContent.toString(),
            containsString("Type Mismatch Error"));
    }

    @Test
    public void doNotLogErrorIfErrorNotTypeMismatch() {
        givenBindingErrorIsMockedToReturnTypeMismatch("error invalid");

        warningLogger.logErrorIfTypeMismatch(mockBindingResult);

        assertThat(outContent.toString().contains("Type Mismatch Error"),
            is(false));
    }

    private void givenBindingErrorIsMockedToReturnTypeMismatch(
        final String code) {
        List<ObjectError> mockErrors = asList(mockObjectError, null);
        when(mockBindingResult.getAllErrors()).thenReturn(mockErrors);
        when(mockObjectError.getCode()).thenReturn(code);
        when(mockObjectError.getDefaultMessage())
            .thenReturn("Type Mismatch Error;");
    }
}
