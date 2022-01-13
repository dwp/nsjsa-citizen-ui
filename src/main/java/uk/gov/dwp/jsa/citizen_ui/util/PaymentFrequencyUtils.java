package uk.gov.dwp.jsa.citizen_ui.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PaymentFrequencyUtils {

    private static final String TYPE_MISMATCH_ERROR = "typeMismatch";

    private PaymentFrequencyUtils() {

    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "False Positive")
    public static boolean isPaymentFrequencyAmountContainInvalidCharacters(final BindingResult bindingResult) {
        if (bindingResult == null) {
            return false;
        }
        List<ObjectError> errors = bindingResult.getAllErrors();

        for (ObjectError error : errors) {
            List<String> errorCodes = Arrays.asList(Objects.requireNonNull(error.getCodes()));
            if (errorCodes.contains(TYPE_MISMATCH_ERROR)) {
                return true;
            }
        }
        return false;
    }
}
