package uk.gov.dwp.jsa.citizen_ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import static org.springframework.util.StringUtils.startsWithIgnoreCase;

/**
 * A Utility class to Log Warnings for unexpected input by user.
 */
@Component
public class WarningLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarningLogger.class);

    private static final String TYPE_MISMATCH_ERROR = "typemismatch";

    public final void logErrorIfTypeMismatch(
        final BindingResult bindingResult) {
        bindingResult.getAllErrors()
                .forEach(WarningLogger::logErrorIfTypeMismatch);
    }

    private static void logErrorIfTypeMismatch(final ObjectError error) {
        if (error != null && error.getCode() != null && startsWithIgnoreCase(
            error.getCode(), TYPE_MISMATCH_ERROR)) {
            String defaultMessage = error.getDefaultMessage();
            if (defaultMessage != null) {
                String[] defaultMessages = defaultMessage.split(";");
                if (defaultMessages.length > 0) {
                    LOGGER.warn(defaultMessages[0]);
                }
            }
        }
    }
}
