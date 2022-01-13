package uk.gov.dwp.testutils;

import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PensionPaymentFrequencyTestUtils {

    public static void verifyUIModel(Model mockModel, String localeErr, boolean hasErrors) {
        int numOfInvocations = hasErrors ? 1 : 0;
        verify(mockModel, times(numOfInvocations))
                .addAttribute("isTypeMismatchErrorPresent", true);
        verify(mockModel, times(numOfInvocations))
                .addAttribute("invalidCharsLocale", localeErr);
    }

    public static List<ObjectError> generateListOfObjectErrorsContainingTypeMismatch(final boolean containTypeMismatchError) {
        ObjectError objectError;
        if (containTypeMismatchError) {
            objectError = new ObjectError("form", new String[]{"typeMismatch"}, null, null);
        } else {
            objectError = new ObjectError("form", new String[]{""}, null, null);
        }
        return Arrays.asList(objectError);
    }
}
