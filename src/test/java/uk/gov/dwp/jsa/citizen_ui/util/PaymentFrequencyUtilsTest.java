package uk.gov.dwp.jsa.citizen_ui.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentFrequencyUtilsTest {

    @Mock
    private BindingResult mockBindingResult;

    @Before
    public void setUp() throws Exception {
        this.mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void isPaymentFrequencyAmountContainInvalidCharacters_bindingResultWithTypeMismatchError_returnsTrue() {
        when(mockBindingResult.getAllErrors()).thenReturn(generateListOfObjectErrors(true));
        boolean response = PaymentFrequencyUtils.isPaymentFrequencyAmountContainInvalidCharacters(mockBindingResult);
        assertTrue(response);
    }

    @Test
    public void isPaymentFrequencyAmountContainInvalidCharacters_bindingResultWithNOTypeMismatchError_returnsFalse() {
        when(mockBindingResult.getAllErrors()).thenReturn(generateListOfObjectErrors(false));
        boolean response = PaymentFrequencyUtils.isPaymentFrequencyAmountContainInvalidCharacters(mockBindingResult);
        assertFalse(response);
    }

    private List<ObjectError> generateListOfObjectErrors(final boolean containTypeMismatchError) {
        ObjectError objectError;
        if (containTypeMismatchError) {
            objectError = new ObjectError("form", new String[]{"typeMismatch"}, null, null);
        } else {
            objectError = new ObjectError("form", new String[]{""}, null, null);
        }
        return Arrays.asList(objectError);
    }
}
