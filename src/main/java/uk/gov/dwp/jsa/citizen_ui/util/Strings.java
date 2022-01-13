package uk.gov.dwp.jsa.citizen_ui.util;

/**
 * This class contains utility methods for questions.
 */
public final class Strings {
    private Strings() { }

    /** @param value the String to be truncated.
     * @param maxLength the maximum length of the returned String
     * @return String the value of the passed in string up to the first maxLength characters. */
    public static String truncate(final String value, final int maxLength) {
        return value == null || value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
